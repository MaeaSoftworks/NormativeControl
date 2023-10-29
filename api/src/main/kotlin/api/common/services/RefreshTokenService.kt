package api.common.services

import api.common.dao.RefreshToken
import api.common.repositories.RefreshTokenRepository
import api.common.repositories.UsersRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val usersRepository: UsersRepository,
    private val tokenGenerator: TokenGenerator
) {

    @Value("\${security.jwt.refreshExpirationMs}")
    private var refreshTokenDurationMs: Long = 0

    fun findByToken(token: String): Mono<RefreshToken> {
        return refreshTokenRepository.getByValue(token)
    }

    fun createRefreshToken(userId: Long): Mono<RefreshToken> {
        return usersRepository
            .getById(userId)
            .flatMap {
                refreshTokenRepository.save(
                    RefreshToken(
                        it.id!!,
                        tokenGenerator.generateToken(64),
                        Instant.now().plusMillis(refreshTokenDurationMs)
                    )
                )
            }
    }

    fun isNotExpired(token: RefreshToken): Boolean {
        return token.expiryDate > Instant.now()
    }
}
