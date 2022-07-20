package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.DocumentCredentials
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<DocumentCredentials, String> {
    fun findByDocumentId(documentId: String): DocumentCredentials?
}
