package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import ru.maeasoftworks.normativecontrol.api.domain.dao.VerificationCode
import ru.maeasoftworks.normativecontrol.api.domain.dao._VerificationCode
import ru.maeasoftworks.normativecontrol.api.domain.dao.verificationCodes
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database

object VerificationCodeRepository : CrudRepository<VerificationCode, Long, _VerificationCode>(Meta.verificationCodes, Meta.verificationCodes.id) {
    suspend fun getByUserId(userId: String): VerificationCode? = getBy(Meta.verificationCodes.userId, userId)

    suspend fun deleteAllByUserId(userId: String) {
        Database {
            runQuery {
                QueryDsl.delete(Meta.verificationCodes).where { Meta.verificationCodes.userId eq userId }
            }
        }
    }
}