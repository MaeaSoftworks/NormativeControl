package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.RefreshToken
import com.maeasoftworks.normativecontrol.dao.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.util.*


interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): Optional<RefreshToken>

    @Modifying
    fun deleteByUser(user: User): Int
}