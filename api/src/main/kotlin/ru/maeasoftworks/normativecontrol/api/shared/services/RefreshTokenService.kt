package ru.maeasoftworks.normativecontrol.api.shared.services

import kotlinx.coroutines.flow.Flow
import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UsersRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val usersRepository: UsersRepository,
    private val tokenGenerator: TokenGenerator
) {

    @Value("\${security.jwt.refreshExpirationMs}")
    private var refreshTokenDurationMs: Long = 0

    fun findByToken(token: String): Flow<RefreshToken> {
        return refreshTokenRepository.getByValue(token)
    }

    suspend fun createRefreshToken(userId: Long): RefreshToken {
        return refreshTokenRepository.save(
            RefreshToken(
                usersRepository.findById(userId).id!!,
                tokenGenerator.generateToken(64),
                Instant.now().plusMillis(refreshTokenDurationMs)
            )
        )
    }

    fun isNotExpired(token: RefreshToken): Boolean {
        return token.expiryDate > Instant.now()
    }
}
