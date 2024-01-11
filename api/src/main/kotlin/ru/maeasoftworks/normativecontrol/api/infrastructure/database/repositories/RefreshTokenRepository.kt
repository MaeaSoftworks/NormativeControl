package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao._RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

object RefreshTokenRepository : CrudRepository<RefreshToken, Long, _RefreshToken>(Meta.refreshTokens, Meta.refreshTokens.id) {
    context(Transaction)
    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? = getBy(Meta.refreshTokens.refreshToken, refreshToken)
}