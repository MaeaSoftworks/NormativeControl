package ru.maeasoftworks.normativecontrol.api.shared.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.shared.utils.CRUDRepository
import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.dao._RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.dao.refreshTokens

object RefreshTokenRepository : CRUDRepository<RefreshToken, Long, _RefreshToken>(Meta.refreshTokens, Meta.refreshTokens.id) {
    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? = getBy(Meta.refreshTokens.refreshToken, refreshToken)
}