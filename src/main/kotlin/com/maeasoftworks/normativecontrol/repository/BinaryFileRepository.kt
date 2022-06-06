package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.BinaryFile
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
@ConditionalOnBean(DocumentManager::class)
interface BinaryFileRepository : JpaRepository<BinaryFile, String> {

    fun findByDocumentId(documentId: String): BinaryFile?

    fun existsBinaryFileByDocumentId(documentId: String): Boolean
}