package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.dtos.Document
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap


@Service
class DocumentQueue {
    private val documentMap: MutableMap<String, Document> = ConcurrentHashMap<String, Document>()
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

    fun removeAt(id: String) {
        documentMap.remove(id)
    }
}
