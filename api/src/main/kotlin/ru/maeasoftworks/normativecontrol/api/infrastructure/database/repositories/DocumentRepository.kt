package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.domain.dao._Document
import ru.maeasoftworks.normativecontrol.api.domain.dao.documents
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

object DocumentRepository : CrudRepository<Document, String, _Document>(Meta.documents, Meta.documents.id) {
    context(Transaction)
    suspend fun getAllByUserId(userId: String) = getAllBy(Meta.documents.userId, userId)

    context(Transaction)
    suspend fun getUserByDocumentId(documentId: String) = getById(documentId)?.userId?.let { UserRepository.getById(it) }
}