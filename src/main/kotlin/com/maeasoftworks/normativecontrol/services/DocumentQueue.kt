package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import com.maeasoftworks.normativecontrol.dtos.DocumentParserRunnable
import com.maeasoftworks.normativecontrol.dtos.enums.State
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class DocumentQueue(private val publisher: ApplicationEventPublisher) {
    private val documentMap: HashMap<String, DocumentParser> = HashMap()
    private val executor: ExecutorService = Executors.newFixedThreadPool(100)
    private var count: IntArray = intArrayOf(0)

    fun put(parser: DocumentParser) {
        documentMap[parser.document.id] = parser
        parser.document.state = State.READY_TO_UPLOAD
    }

    fun runById(documentId: String) {
        val parser = documentMap[documentId]
        if (parser != null) {
            count[0]++
            parser.document.state = State.PROCESSING
            executor.execute(DocumentParserRunnable(parser, count, publisher))
        }
    }

    fun isUploadAvailable(documentId: String): Boolean {
        return documentMap[documentId]?.document?.state == State.READY_TO_UPLOAD && count[0] < 100
    }

    fun getById(id: String): DocumentParser? {
        return documentMap[id]
    }

    fun remove(id: String) {
        documentMap.remove(id)
    }
}
