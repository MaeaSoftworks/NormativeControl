package com.maeasoftworks.tellurium.services

import com.maeasoftworks.tellurium.dao.RefreshToken
import com.maeasoftworks.tellurium.repository.RefreshTokenRepository
import com.maeasoftworks.tellurium.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository
) {
    @Value("\${app.jwtRefreshExpirationMs}")
    private var refreshTokenDurationMs: Long = 0

    fun findByToken(token: String): Optional<RefreshToken> {
        return refreshTokenRepository.findByRefreshToken(token)
    }

    fun createRefreshToken(userId: Long): RefreshToken {
        return refreshTokenRepository.save(
            RefreshToken(
                userRepository.findById(userId).get(),
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(refreshTokenDurationMs)
            )
        )
    }

    fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(token)
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Refresh token was expired. Please make a new sign in request"
            )
        }
        return token
    }

    @Transactional
    fun deleteByUserId(userId: Long): Int {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get())
    }
}
