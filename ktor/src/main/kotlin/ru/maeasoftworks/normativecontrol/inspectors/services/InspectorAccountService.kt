package ru.maeasoftworks.normativecontrol.inspectors.services

import at.favre.lib.crypto.bcrypt.BCrypt
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.inspectors.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.shared.dao.User
import ru.maeasoftworks.normativecontrol.shared.exceptions.AuthenticationException
import ru.maeasoftworks.normativecontrol.shared.exceptions.OutdatedRefreshToken
import ru.maeasoftworks.normativecontrol.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.shared.services.RefreshTokenService
import ru.maeasoftworks.normativecontrol.shared.utils.Service
import java.time.Instant

class InspectorAccountService(override val di: DI): Service() {
    private val userRepository: UserRepository by instance()


    suspend fun authenticate(loginRequest: LoginRequest): User {
        val user = userRepository.getUserByUsername(loginRequest.username) ?: throw AuthenticationException()
        if (!(BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password).verified)) {
            throw AuthenticationException()
        }
        return user
    }
}