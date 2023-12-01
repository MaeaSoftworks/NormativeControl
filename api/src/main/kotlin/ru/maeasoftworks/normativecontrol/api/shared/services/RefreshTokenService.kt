package ru.maeasoftworks.normativecontrol.api.shared.services

import io.ktor.server.application.Application
import kotlinx.coroutines.flow.Flow
import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.InvalidRefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.OutdatedRefreshToken
import ru.maeasoftworks.normativecontrol.api.shared.repositories.RefreshTokenRepository
import java.security.SecureRandom
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenService @Inject constructor(
    private val refreshTokenRepository: RefreshTokenRepository,
    application: Application
) {
    private val secureRandom = SecureRandom()

    private val refreshTokenExpiration = application.environment.config.property("jwt.refreshTokenExpiration").getString().toLong()

    suspend fun updateJwtToken(refreshToken: String, userAgent: String?): RefreshToken {
        val token = refreshTokenRepository.getRefreshTokenByValue(refreshToken)
        if (token != null) {
            refreshTokenRepository.delete(token.id)
            if (token.expiresAt >= Instant.now()) {
                return createRefreshTokenAndSave(token.userId, userAgent)
            }
            throw OutdatedRefreshToken()
        }
        throw InvalidRefreshToken()
    }

    suspend fun createRefreshTokenAndSave(userId: Long, userAgent: String?): RefreshToken {
        return refreshTokenRepository.save(createRefreshToken(userId, userAgent))
    }

    private fun createRefreshToken(userId: Long, userAgent: String?): RefreshToken {
        return RefreshToken(
            refreshToken = createRefreshTokenString(),
            expiresAt = Instant.now().plusSeconds(refreshTokenExpiration),
            userId = userId,
            createdAt = Instant.now(),
            userAgent = userAgent
        )
    }

    private fun createRefreshTokenString(): String {
        return (0..32).map { letters[secureRandom.nextInt(lettersLength)] }.joinToString("")
    }

    suspend fun getAllRefreshTokensOfUser(userId: Long): Flow<RefreshToken> =
        refreshTokenRepository.getAllBy(Meta.refreshTokens.userId, userId)

    companion object {
        val letters = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "!@#$%^&*_".toCharArray()
        val lettersLength = letters.size
    }
}