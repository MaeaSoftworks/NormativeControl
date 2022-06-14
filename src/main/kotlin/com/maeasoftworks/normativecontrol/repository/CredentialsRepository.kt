package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.DocumentCredentials
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<DocumentCredentials, String> {
    fun findByDocumentId(documentId: String): DocumentCredentials?
}
