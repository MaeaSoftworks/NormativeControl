@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package ru.maeasoftworks.normativecontrol.api.shared.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Mono
import ru.maeasoftworks.normativecontrol.api.shared.dao.User

interface UsersRepository : CoroutineCrudRepository<User, Long> {

    fun getByUsername(username: String): Flow<User>

    override suspend fun findById(id: Long): User

    fun getById(id: Long): Mono<User>

    fun existsByUsername(username: String): Flow<Boolean>
}
