package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.daos.DocumentError
import org.springframework.data.repository.CrudRepository

interface DocumentErrorRepository : CrudRepository<DocumentError, String> {
    fun findAllByDocumentId(documentId: String): List<DocumentError>
}