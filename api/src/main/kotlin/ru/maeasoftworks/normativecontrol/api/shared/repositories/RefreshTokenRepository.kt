package ru.maeasoftworks.normativecontrol.api.shared.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.app.Repository
import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.dao._RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.dao.refreshTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenRepository @Inject constructor() : Repository<RefreshToken, Long, _RefreshToken>(Meta.refreshTokens, Meta.refreshTokens.id) {
    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? = getBy(Meta.refreshTokens.refreshToken, refreshToken)
}