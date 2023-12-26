package ru.maeasoftworks.normativecontrol.api.domain.services

import at.favre.lib.crypto.bcrypt.BCrypt
import ru.maeasoftworks.normativecontrol.api.app.web.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.AuthenticationException
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository

object AccountService {
    suspend fun authenticate(loginRequest: LoginRequest): User {
        val user = UserRepository.getUserByUsername(loginRequest.username) ?: throw AuthenticationException()
        if (!(BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password).verified)) {
            throw AuthenticationException()
        }
        return user
    }

    suspend fun changePassword(userId: Long, newPassword: String) {
        UserRepository.update(userId) {
            this.password = BCrypt.withDefaults().hashToString(10, newPassword.toCharArray())
        }
    }

    suspend fun changeUsername(userId: Long, newUsername: String) {
        UserRepository.update(userId) {
            this.username = newUsername
        }
    }
}