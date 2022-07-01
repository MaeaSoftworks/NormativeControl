package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.docx4nc.enums.Status
import com.maeasoftworks.docx4nc.model.FailureType
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.dao.DocumentBytes
import com.maeasoftworks.normativecontrol.dao.DocumentCredentials
import com.maeasoftworks.normativecontrol.dto.Document
import com.maeasoftworks.normativecontrol.dto.DocumentParserRunnable
import com.maeasoftworks.normativecontrol.dto.EnqueuedParser
import com.maeasoftworks.normativecontrol.repository.BinaryFileRepository
import com.maeasoftworks.normativecontrol.repository.CredentialsRepository
import com.maeasoftworks.normativecontrol.repository.MistakeRepository
import com.maeasoftworks.normativecontrol.utils.toDao
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
            queue.remove(order.document.id)
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return queue[documentId]?.document?.data?.status == Status.READY_TO_ENQUEUE && count.get() < 100
    }

    operator fun get(id: String) = queue[id]
}
