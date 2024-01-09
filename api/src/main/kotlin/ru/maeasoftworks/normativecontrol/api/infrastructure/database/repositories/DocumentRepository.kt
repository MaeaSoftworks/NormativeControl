package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.domain.dao._Document
import ru.maeasoftworks.normativecontrol.api.domain.dao.documents

object DocumentRepository : CrudRepository<Document, String, _Document>(Meta.documents, Meta.documents.id) {
    suspend fun getAllByUserId(userId: String) = getAllBy(Meta.documents.userId, userId)
}