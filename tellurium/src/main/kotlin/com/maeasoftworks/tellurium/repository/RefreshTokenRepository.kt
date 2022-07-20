package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.RefreshToken
import com.maeasoftworks.tellurium.dao.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.util.*

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByRefreshToken(token: String): Optional<RefreshToken>

    @Modifying
    fun deleteByUser(user: User): Int
}
