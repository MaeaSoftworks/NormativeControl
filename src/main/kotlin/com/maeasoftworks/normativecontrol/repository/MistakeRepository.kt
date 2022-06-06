package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.Mistake
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository

@ConditionalOnBean(DocumentManager::class)
interface MistakeRepository : CrudRepository<Mistake, String> {
    fun findAllByDocumentId(documentId: String): List<Mistake>
}