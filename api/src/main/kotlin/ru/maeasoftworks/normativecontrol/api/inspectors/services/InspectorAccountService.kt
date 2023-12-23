package ru.maeasoftworks.normativecontrol.api.inspectors.services

import at.favre.lib.crypto.bcrypt.BCrypt
import ru.maeasoftworks.normativecontrol.api.inspectors.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.api.shared.dao.User
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.AuthenticationException
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UserRepository

object InspectorAccountService {
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