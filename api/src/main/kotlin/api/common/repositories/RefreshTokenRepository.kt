package api.common.repositories

import api.common.dao.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, Long> {
    fun getByValue(token: String): Mono<RefreshToken>

    fun deleteByUserId(userId: Long): Mono<Long>

    fun deleteByValue(refreshToken: String): Mono<Long>
}
