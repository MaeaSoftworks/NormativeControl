package com.maeasoftworks.tellurium.services

import com.maeasoftworks.polonium.enums.Status
import com.maeasoftworks.polonium.enums.FailureType
import com.maeasoftworks.polonium.parsers.DocumentParser
import com.maeasoftworks.tellurium.dao.Document
import com.maeasoftworks.tellurium.dto.DocumentDTO
import com.maeasoftworks.tellurium.dto.DocumentParserRunnable
import com.maeasoftworks.tellurium.dto.EnqueuedParser
import com.maeasoftworks.tellurium.repository.DocumentsRepository
import com.maeasoftworks.tellurium.utils.dao
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class DocumentQueue(
    private val documentsRepository: DocumentsRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    private val queue: HashMap<String, EnqueuedParser> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(100) { r -> Thread(r, "DocumentParser") }
    var count: AtomicInteger = AtomicInteger(0)

    fun put(parser: DocumentParser, documentDTO: DocumentDTO) {
        queue[documentDTO.id] = EnqueuedParser(parser, documentDTO)
        parser.documentData.status = Status.READY_TO_ENQUEUE
    }

    fun run(documentId: String) {
        val order = queue[documentId]
        if (order != null) {
            count.incrementAndGet()
            order.documentDTO.data.status = Status.PROCESSING
            executor.execute(DocumentParserRunnable(order, count, ::saveToDatabase))
        }
    }

    @Transactional
    fun saveToDatabase(order: EnqueuedParser) {
        if (order.documentDTO.data.failureType == FailureType.NONE) {
            documentsRepository.save(
                Document(
                    order.documentDTO.id,
                    bCryptPasswordEncoder.encode(order.documentDTO.accessKey),
                    order.documentDTO.password,
                    order.documentDTO.data.file,
                    order.render,
                    order.documentParser.mistakes.map { it.dao }
                )
            )
            queue.remove(order.documentDTO.id)
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return queue[documentId]?.documentDTO?.data?.status == Status.READY_TO_ENQUEUE && count.get() < 100
    }

    operator fun get(id: String) = queue[id]
}
