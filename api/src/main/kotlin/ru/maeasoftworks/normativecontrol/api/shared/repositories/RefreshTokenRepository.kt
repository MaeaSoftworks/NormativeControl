package ru.maeasoftworks.normativecontrol.api.shared.repositories

import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, Long> {
    fun getByValue(token: String): Mono<RefreshToken>

    fun deleteByUserId(userId: Long): Mono<Long>

    fun deleteByValue(refreshToken: String): Mono<Long>
}
