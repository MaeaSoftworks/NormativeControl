package api.common.repositories

import api.common.dao.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UsersRepository : ReactiveCrudRepository<User, Long> {

    fun getByUsername(username: String): Mono<User>

    fun getById(id: Long): Mono<User>

    fun existsByUsername(username: String): Mono<Boolean>
}