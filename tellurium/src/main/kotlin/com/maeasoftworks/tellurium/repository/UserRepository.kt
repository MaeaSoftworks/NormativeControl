package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>

    fun existsByEmail(email: String): Boolean
}
