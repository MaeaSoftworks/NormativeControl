package ru.maeasoftworks.normativecontrol.api.infrastructure.security

import io.ktor.server.application.Application
import kotlinx.coroutines.flow.Flow
import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRefreshToken
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.OutdatedRefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao.refreshTokens
import java.security.SecureRandom
import java.time.Instant

object JwtRefreshToken: Module {

    private val secureRandom = SecureRandom()
    private var refreshTokenExpiration: Long = 0
    private val letters = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "!@#$%^&*_".toCharArray()
    private val lettersLength = letters.size

    override fun Application.module() {
        refreshTokenExpiration = environment.config.property("jwt.refreshTokenExpiration").getString().toLong()
    }

    suspend fun updateJwtToken(refreshToken: String, userAgent: String?): RefreshToken {
        val token = RefreshTokenRepository.getRefreshTokenByValue(refreshToken)
        if (token != null) {
            RefreshTokenRepository.delete(token.id)
            if (token.expiresAt >= Instant.now()) {
                return createRefreshTokenAndSave(token.userId, userAgent)
            }
            throw OutdatedRefreshToken()
        }
        throw InvalidRefreshToken()
    }

    suspend fun createRefreshTokenAndSave(userId: Long, userAgent: String?): RefreshToken {
        return RefreshTokenRepository.save(
            RefreshToken(
                refreshToken = createRefreshTokenString(),
                expiresAt = Instant.now().plusSeconds(refreshTokenExpiration),
                userId = userId,
                createdAt = Instant.now(),
                userAgent = userAgent
            )
        )
    }

    private fun createRefreshTokenString(): String {
        return (0..32).map { letters[secureRandom.nextInt(lettersLength)] }.joinToString("")
    }

    suspend fun getAllRefreshTokensOfUser(userId: Long): Flow<RefreshToken> = RefreshTokenRepository.getAllBy(Meta.refreshTokens.userId, userId)
}