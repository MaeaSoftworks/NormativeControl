package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.entities.BinaryFile
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository

@ConditionalOnBean(DocumentManager::class)
interface BinaryFileRepository : CrudRepository<BinaryFile, String> {
    fun findByDocumentId(documentId: String): BinaryFile?

    fun existsBinaryFileByDocumentId(documentId: String): Boolean
}