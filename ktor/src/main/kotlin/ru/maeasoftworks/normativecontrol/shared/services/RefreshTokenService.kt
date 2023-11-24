package ru.maeasoftworks.normativecontrol.shared.services

import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.shared.exceptions.InvalidRefreshToken
import ru.maeasoftworks.normativecontrol.shared.exceptions.OutdatedRefreshToken
import ru.maeasoftworks.normativecontrol.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.shared.utils.Service
import java.security.SecureRandom
import java.time.Instant

class RefreshTokenService(override val di: DI): Service() {
    private val secureRandom = SecureRandom()
    private val refreshTokenRepository: RefreshTokenRepository by instance()

    suspend fun updateJwtToken(refreshToken: String): RefreshToken {
        val token = refreshTokenRepository.getRefreshTokenByValue(refreshToken)
        if (token != null) {
            refreshTokenRepository.deleteRefreshToken(token)
            if (token.expiresAt < Instant.now()) {
                throw OutdatedRefreshToken()
            } else {
                return createRefreshTokenAndSave(token.userId)
            }
        } else {
            throw InvalidRefreshToken()
        }
    }

    suspend fun createRefreshTokenAndSave(userId: Long): RefreshToken {
        return refreshTokenRepository.saveRefreshToken(createRefreshToken(userId))
    }

    private fun createRefreshToken(userId: Long): RefreshToken {
        return RefreshToken(
            refreshToken = createRefreshTokenString(),
            expiresAt = Instant.now().plusMillis(30L * 24 * 60 * 60 * 1000),
            userId = userId
        )
    }

    private fun createRefreshTokenString(): String {
        return (0..32).map { letters[secureRandom.nextInt(lettersLength)] }.joinToString("")
    }

    companion object {
        val letters = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "!@#$%^&*_".toCharArray()
        val lettersLength = letters.size
    }
}