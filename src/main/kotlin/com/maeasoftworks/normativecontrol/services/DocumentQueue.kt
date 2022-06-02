package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.parser.enums.Status
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParserRunnable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
@ConditionalOnBean(DocumentManager::class)
class DocumentQueue @Autowired constructor(private val publisher: ApplicationEventPublisher) {
    private val documentMap: HashMap<String, DocumentParser> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(100)
    var count: AtomicInteger = AtomicInteger(0)

    fun put(parser: DocumentParser) {
        documentMap[parser.document.id] = parser
        parser.document.status = Status.READY_TO_ENQUEUE
    }

    fun runById(documentId: String) {
        val parser = documentMap[documentId]
        if (parser != null) {
            count.incrementAndGet()
            parser.document.status = Status.PROCESSING
            executor.execute(DocumentParserRunnable(parser, count, publisher))
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return documentMap[documentId]?.document?.status == Status.READY_TO_ENQUEUE && count.get() < 100
    }

    fun getById(id: String): DocumentParser? {
        return documentMap[id]
    }

    fun remove(id: String) {
        documentMap.remove(id)
    }
}
