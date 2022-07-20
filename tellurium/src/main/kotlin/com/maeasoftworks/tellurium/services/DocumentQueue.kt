package com.maeasoftworks.tellurium.services

import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.polonium.model.FailureType
import com.maeasoftworks.polonium.parsers.DocumentParser
import com.maeasoftworks.tellurium.dao.DocumentBytes
import com.maeasoftworks.tellurium.dao.DocumentCredentials
import com.maeasoftworks.tellurium.dao.DocumentRender
import com.maeasoftworks.tellurium.dto.Document
import com.maeasoftworks.tellurium.dto.DocumentParserRunnable
import com.maeasoftworks.tellurium.dto.EnqueuedParser
import com.maeasoftworks.tellurium.repository.BinaryFileRepository
import com.maeasoftworks.tellurium.repository.CredentialsRepository
import com.maeasoftworks.tellurium.repository.HtmlRepository
import com.maeasoftworks.tellurium.repository.MistakeRepository
import com.maeasoftworks.tellurium.utils.toDao
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class DocumentQueue(
    private val mistakeRepository: MistakeRepository,
    private val fileRepository: BinaryFileRepository,
    private val htmlRepository: HtmlRepository,
    private val credentialsRepository: CredentialsRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    private val queue: HashMap<String, EnqueuedParser> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(100) { r -> Thread(r, "DocumentParser") }
    var count: AtomicInteger = AtomicInteger(0)

    fun put(parser: DocumentParser, document: Document) {
        queue[document.id] = EnqueuedParser(parser, document)
        parser.documentData.status = Status.READY_TO_ENQUEUE
    }

    fun run(documentId: String) {
        val order = queue[documentId]
        if (order != null) {
            count.incrementAndGet()
            order.document.data.status = Status.PROCESSING
            executor.execute(DocumentParserRunnable(order, count, ::saveToDatabase))
        }
    }

    @Transactional
    fun saveToDatabase(order: EnqueuedParser) {
        if (order.document.data.failureType == FailureType.NONE) {
            fileRepository.save(DocumentBytes(order.document.id, order.document.data.file))
            mistakeRepository.saveAll(order.documentParser.mistakes.map { it.toDao(order.document.id) })
            credentialsRepository.save(
                DocumentCredentials(
                    order.document.id,
                    bCryptPasswordEncoder.encode(order.document.accessKey),
                    order.document.password
                )
            )
            htmlRepository.save(DocumentRender(order.document.id, order.render!!))
            queue.remove(order.document.id)
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return queue[documentId]?.document?.data?.status == Status.READY_TO_ENQUEUE && count.get() < 100
    }

    operator fun get(id: String) = queue[id]
}
