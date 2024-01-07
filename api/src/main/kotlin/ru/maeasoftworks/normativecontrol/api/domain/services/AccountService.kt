package ru.maeasoftworks.normativecontrol.api.domain.services

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.Application
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.app.web.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.api.app.web.dto.RegistrationRequest
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.dao.VerificationCode
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.VerificationCodeRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.*
import java.security.SecureRandom
import java.time.Instant

object AccountService : Module {
    private val random = SecureRandom()
    private var verificationCodeExpiration: Long = 0
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun Application.module() {
        verificationCodeExpiration = environment.config.property("security.verification.expirationSeconds").getString().toLong()
    }

    suspend fun register(registrationRequest: RegistrationRequest): User = transaction {
        val registered = UserRepository.getUserByEmail(registrationRequest.email)
        var id: String
        while (true) {
            id = KeyGenerator.generate(16)
            if (!UserRepository.existById(id)) {
                break
            }
        }
        if (registered != null) throw CredentialsIsAlreadyInUseException()
        return@transaction UserRepository.save(
            User(
                id = id,
                email = registrationRequest.email,
                password = BCrypt.withDefaults().hashToString(10, registrationRequest.password.toCharArray()),
                role = Role.STUDENT
            )
        )
    }

    suspend fun authenticate(loginRequest: LoginRequest): User = transaction {
        val user = UserRepository.getUserByEmail(loginRequest.email) ?: throw AuthenticationException()
        if (!(BCrypt.verifyer().verify(loginRequest.password.toCharArray(), user.password).verified)) {
            throw AuthenticationException()
        }
        return@transaction user
    }

    suspend fun changePassword(userId: String, newPassword: String) = transaction {
        UserRepository.update(userId) {
            password = BCrypt.withDefaults().hashToString(10, newPassword.toCharArray())
        }
    }

    suspend fun changeEmail(userId: String, newEmail: String) = transaction {
        UserRepository.update(userId) {
            email = newEmail
            isCredentialsVerified = false
        }
    }

    suspend fun createVerificationCode(userId: String): Pair<Instant, Instant> = transaction {
        if (UserRepository.getById(userId)?.isCredentialsVerified ?: throw EntityNotFoundException("User")) {
            throw InconsistentStateException("Email is already verified")
        }
        VerificationCodeRepository.deleteAllByUserId(userId)
        val code = random.nextInt(100_000, 1_000_000)
        logger.warn("Verification code for userId=$userId: $code") // TODO replace with email or something
        val createdAt = Instant.now()
        val expiredAt = createdAt.plusSeconds(verificationCodeExpiration)
        VerificationCodeRepository.save(VerificationCode(userId = userId, code = code, createdAt = createdAt, expiresAt = expiredAt))
        return@transaction createdAt to expiredAt
    }

    suspend fun verify(userId: String, verificationCode: Int): Boolean = transaction {
        val code = VerificationCodeRepository.getByUserId(userId) ?: throw EntityNotFoundException("Valid verification code")
        if (code.expiresAt < Instant.now()) {
            throw OutdatedException("Verification code")
        }
        if (code.code != verificationCode) {
            return@transaction false
        }
        VerificationCodeRepository.delete(code.id)
        UserRepository.update(userId) {
            this.isCredentialsVerified = true
        }
        return@transaction true
    }
}