package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.Mistake
import org.springframework.data.repository.CrudRepository

interface MistakeRepository : CrudRepository<Mistake, String> {
    fun findAllByDocumentId(documentId: String): List<Mistake>

    fun deleteAllByDocumentId(documentId: String)
}
