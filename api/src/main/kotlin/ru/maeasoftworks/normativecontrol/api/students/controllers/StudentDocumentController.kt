package ru.maeasoftworks.normativecontrol.api.students.controllers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.reactive.asFlow
import ru.maeasoftworks.normativecontrol.api.shared.services.TokenGenerator
import ru.maeasoftworks.normativecontrol.api.students.components.DocumentManager
import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import ru.maeasoftworks.normativecontrol.api.shared.takeWhileInclusive
import java.nio.ByteBuffer
import java.time.Duration
import java.util.*

@RestController
@RequestMapping("/student/document")
@CrossOrigin
class StudentDocumentController(
    private val documentManager: DocumentManager,
    private val tokenGenerator: TokenGenerator
) {
    @PostMapping("/upload", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun receive(@RequestPart("file") filePart: FilePart, exchange: ServerWebExchange): Flow<Message> {
        val documentId = UUID.randomUUID().toString().filter { it != '-' }
        val accessKey = tokenGenerator.generateToken(32)
        exchange.response.addCookie(ResponseCookie.from("accessKey", accessKey).maxAge(Duration.ofDays(30)).build())
        return merge(
            documentManager.saveDocumentAndSubscribe(filePart, documentId, accessKey),
            Flux.interval(Duration.ofSeconds(1)).map { Message(documentId, Message.Code.INFO, "Working") }.asFlow()
        ).takeWhileInclusive {
            it.code != Message.Code.ERROR && it.code != Message.Code.SUCCESS
        }
    }

    @GetMapping("/conclusion", produces = [MediaType.TEXT_HTML_VALUE])
    fun getConclusion(@RequestParam documentId: String, @CookieValue("accessKey") accessKey: String): Flow<ByteBuffer> {
        return documentManager.checkAccessAndGetHtml(documentId, accessKey)
    }

    @GetMapping("/download", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun download(@RequestParam documentId: String, @CookieValue("accessKey") accessKey: String): Flow<ByteBuffer> {
        return documentManager.checkAccessAndGetConclusion(documentId, accessKey)
    }
}