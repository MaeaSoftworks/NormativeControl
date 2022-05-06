package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.daos.DocumentFile
import org.springframework.data.repository.CrudRepository

interface DocumentFileRepository : CrudRepository<DocumentFile, String> {
    fun findAllByDocumentId(documentId: String): List<DocumentFile>

    fun existsDocumentFileByDocumentId(documentId: String): Boolean
}