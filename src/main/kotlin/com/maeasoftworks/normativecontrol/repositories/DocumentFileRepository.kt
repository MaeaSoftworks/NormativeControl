package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.entities.DocumentFile
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository

@ConditionalOnBean(DocumentManager::class)
interface DocumentFileRepository : CrudRepository<DocumentFile, String> {
    fun findByDocumentId(documentId: String): DocumentFile?

    fun existsDocumentFileByDocumentId(documentId: String): Boolean
}