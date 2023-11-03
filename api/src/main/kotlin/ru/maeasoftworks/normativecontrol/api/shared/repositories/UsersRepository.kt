@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package ru.maeasoftworks.normativecontrol.api.shared.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.maeasoftworks.normativecontrol.api.shared.dao.User
import reactor.core.publisher.Mono

interface UsersRepository : CoroutineCrudRepository<User, Long> {

    fun getByUsername(username: String): Flow<User>

    override suspend fun findById(id: Long): User

    fun getById(id: Long): Mono<User>

    fun existsByUsername(username: String): Flow<Boolean>
}