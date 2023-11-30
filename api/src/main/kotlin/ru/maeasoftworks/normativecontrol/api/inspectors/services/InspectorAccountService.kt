package ru.maeasoftworks.normativecontrol.api.inspectors.services

import at.favre.lib.crypto.bcrypt.BCrypt
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.api.inspectors.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.api.shared.dao.User
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.AuthenticationException
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.shared.utils.Service

class InspectorAccountService(override val di: DI) : Service() {
    private val userRepository: UserRepository by instance()

    suspend fun authenticate(loginRequest: LoginRequest): User {
        val user = userRepository.getUserByUsername(loginRequest.username) ?: throw AuthenticationException()
        if (!(BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password).verified)) {
            throw AuthenticationException()
        }
        return user
    }

    suspend fun changePassword(userId: Long, newPassword: String) {
        userRepository.update(userId) {
            this.password = BCrypt.withDefaults().hashToString(10, newPassword.toCharArray())
        }
    }

    suspend fun changeUsername(userId: Long, newUsername: String) {
        userRepository.update(userId) {
            this.username = newUsername
        }
    }
}