package api.students.controllers

import api.common.services.TokenGenerator
import api.students.components.DocumentManager
import api.students.dto.Message
import api.students.dto.MessageCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import java.nio.ByteBuffer
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("/student/document")
@CrossOrigin
class DocumentController(
    private val documentManager: DocumentManager,
    private val tokenGenerator: TokenGenerator
) {
    @PostMapping("/upload", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun receive(@RequestPart("file") filePart: FilePart, exchange: ServerWebExchange): Flux<Message> {
        val documentId = UUID.randomUUID().toString().filter { it != '-' }
        val accessKey = tokenGenerator.generateToken(32)
        exchange.response.addCookie(ResponseCookie.from("accessKey", accessKey).maxAge(Duration.ofDays(30)).build())
        val documentFlux = documentManager.saveDocumentAndSubscribe(filePart, documentId, accessKey)
        val infoFlux = Flux
            .interval(Duration.ofSeconds(1))
            .map { Message(documentId, MessageCode.INFO, "Working") }

        return Flux
            .merge(documentFlux, infoFlux)
            .takeUntil { it.code == MessageCode.ERROR || it.code == MessageCode.SUCCESS }
    }

    @GetMapping("/conclusion", produces = [MediaType.TEXT_HTML_VALUE])
    fun getConclusion(@RequestParam documentId: String, @CookieValue("accessKey") accessKey: String): Flux<ByteBuffer> {
        return documentManager.checkAccessAndGetHtml(documentId, accessKey)
    }

    @GetMapping("/download", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun download(@RequestParam documentId: String, @CookieValue("accessKey") accessKey: String): Flux<ByteBuffer> {
        return documentManager.checkAccessAndGetConclusion(documentId, accessKey)
    }
}