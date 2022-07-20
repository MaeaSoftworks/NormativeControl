package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.Mistake
import org.springframework.data.repository.CrudRepository

interface MistakeRepository : CrudRepository<Mistake, String> {
    fun findAllByDocumentId(documentId: String): List<Mistake>

    fun deleteAllByDocumentId(documentId: String)
}
