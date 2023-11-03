package ru.maeasoftworks.normativecontrol.api.shared.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.maeasoftworks.normativecontrol.api.shared.dao.RefreshToken

interface RefreshTokenRepository : CoroutineCrudRepository<RefreshToken, Long> {
    fun getByValue(token: String): Flow<RefreshToken>

    override suspend fun deleteById(id: Long)

    override suspend fun delete(entity: RefreshToken)

    suspend fun deleteByUserId(userId: Long)
}
