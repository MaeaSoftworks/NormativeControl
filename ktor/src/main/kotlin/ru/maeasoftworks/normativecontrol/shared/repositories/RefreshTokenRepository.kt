package ru.maeasoftworks.normativecontrol.shared.repositories

import org.kodein.di.DI
import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.shared.dao._RefreshToken
import ru.maeasoftworks.normativecontrol.shared.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.shared.utils.Repository

class RefreshTokenRepository(override val di: DI) : Repository<RefreshToken, Long, _RefreshToken>(Meta.refreshTokens, Meta.refreshTokens.id) {
    suspend fun getRefreshTokenByValue(refreshToken: String): RefreshToken? = getBy(Meta.refreshTokens.refreshToken, refreshToken)
}