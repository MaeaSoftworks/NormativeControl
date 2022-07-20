package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.DocumentBytes
import org.springframework.data.jpa.repository.JpaRepository

interface BinaryFileRepository : JpaRepository<DocumentBytes, String> {

    fun findByDocumentId(documentId: String): DocumentBytes?

    fun existsBinaryFileByDocumentId(documentId: String): Boolean
}
