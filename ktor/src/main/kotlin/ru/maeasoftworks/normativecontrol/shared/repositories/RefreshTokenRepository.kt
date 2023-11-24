package ru.maeasoftworks.normativecontrol.shared.repositories

import org.kodein.di.DI
import org.kodein.di.instance
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.firstOrNull
import ru.maeasoftworks.normativecontrol.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.shared.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.shared.modules.Database
import ru.maeasoftworks.normativecontrol.shared.utils.Repository

class RefreshTokenRepository(override val di: DI) : Repository() {
    private val database: Database by instance()

    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? {
        return database.runQuery {
            QueryDsl.from(Meta.refreshTokens).where { Meta.refreshTokens.refreshToken eq refreshToken }.firstOrNull()
        }
    }

    suspend fun deleteRefreshToken(refreshToken: RefreshToken) {
        database.runQuery {
            QueryDsl.delete(Meta.refreshTokens).single(refreshToken)
        }
    }

    suspend fun saveRefreshToken(refreshToken: RefreshToken): RefreshToken {
        return database.runQuery {
            QueryDsl.insert(Meta.refreshTokens).single(refreshToken)
        }
    }
}