package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.dto.Document
import com.maeasoftworks.normativecontrol.dto.DocumentParserRunnable
import com.maeasoftworks.normativecontrol.dto.OrderedParser
import com.maeasoftworks.normativecontrol.dto.Status
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
@ConditionalOnBean(DocumentManager::class)
class DocumentQueue(private val publisher: ApplicationEventPublisher) {
    private val documentMap: HashMap<String, OrderedParser> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(100)
    var count: AtomicInteger = AtomicInteger(0)

    fun put(parser: DocumentParser, document: Document) {
        documentMap[document.id] = OrderedParser(parser, document)
        parser.documentData.status = Status.READY_TO_ENQUEUE
    }

    fun runById(documentId: String) {
        val order = documentMap[documentId]
        if (order != null) {
            count.incrementAndGet()
            order.document.data.status = Status.PROCESSING
            executor.execute(DocumentParserRunnable(order, count, publisher))
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return documentMap[documentId]?.document?.data?.status == Status.READY_TO_ENQUEUE && count.get() < 100
    }

    fun getById(id: String): OrderedParser? {
        return documentMap[id]
    }

    fun remove(id: String) {
        documentMap.remove(id)
    }
}
