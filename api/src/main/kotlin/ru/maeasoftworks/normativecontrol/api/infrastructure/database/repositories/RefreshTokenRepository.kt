package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import org.komapper.r2dbc.R2dbcDatabase
import ru.maeasoftworks.normativecontrol.api.domain.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao._RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao.refreshTokens

object RefreshTokenRepository : CrudRepository<RefreshToken, Long, _RefreshToken>(Meta.refreshTokens, Meta.refreshTokens.id) {
    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? = getBy(Meta.refreshTokens.refreshToken, refreshToken)
}