package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.operator.count
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.domain.dao._Document
import ru.maeasoftworks.normativecontrol.api.domain.dao.documents
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.UnsafeDataAccess
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

object DocumentRepository : CrudRepository<Document, String, _Document>(Meta.documents, Meta.documents.id) {
    context(Transaction)
    suspend fun getAllByUserId(userId: String) = getAllBy(Meta.documents.userId, userId)

    context(Transaction)
    suspend fun isUserOwnerOf(userId: String, documentId: String): Boolean {
        return Database {
            runQuery {
                QueryDsl.from(Meta.documents).where {
                    and {
                        Meta.documents.id eq documentId
                        Meta.documents.userId eq userId
                    }
                }.select(count())
            } == 1L
        }
    }

    context(Transaction)
    @OptIn(UnsafeDataAccess::class)
    suspend fun getUserByDocumentId(documentId: String) = getById(documentId)?.userId?.let { UserRepository.getById(it) }
}