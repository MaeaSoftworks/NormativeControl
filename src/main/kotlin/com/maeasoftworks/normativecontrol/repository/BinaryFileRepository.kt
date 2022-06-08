package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.DocumentBytes
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
@ConditionalOnBean(DocumentManager::class)
interface BinaryFileRepository : JpaRepository<DocumentBytes, String> {

    fun findByDocumentId(documentId: String): DocumentBytes?

    fun existsBinaryFileByDocumentId(documentId: String): Boolean
}
