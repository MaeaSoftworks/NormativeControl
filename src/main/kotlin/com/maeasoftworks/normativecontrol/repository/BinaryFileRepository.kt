package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.DocumentBytes
import org.springframework.data.jpa.repository.JpaRepository

interface BinaryFileRepository : JpaRepository<DocumentBytes, String> {

    fun findByDocumentId(documentId: String): DocumentBytes?

    fun existsBinaryFileByDocumentId(documentId: String): Boolean
}
