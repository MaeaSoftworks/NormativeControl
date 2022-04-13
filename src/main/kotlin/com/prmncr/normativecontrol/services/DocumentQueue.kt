package com.prmncr.normativecontrol.services

import com.prmncr.normativecontrol.dtos.Document
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class DocumentQueue {
    private val documentMap: MutableMap<String, Document> = ConcurrentHashMap()
    fun put(document: Document) {
        documentMap[document.id] = document
    }

    @Nullable
    fun getById(id: String): Document? {
        return try {
            documentMap[id]
        } catch (e: NullPointerException) {
            null
        }
    }

    fun remove(id: String) {
        documentMap.remove(id)
    }
}