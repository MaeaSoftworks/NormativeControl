package ru.maeasoftworks.normativecontrol.api.domain.services

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.api.domain.Organization
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.UnsafeDatabaseUsage
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.unsafeDatabaseUsage
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object StandaloneInspector: Module {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(UnsafeDatabaseUsage::class)
    override fun Application.module(): Unit = runBlocking {
        val email = "inspector@urfu.ru"
        val password = "inspector"
        logger.info("Created INSPECTOR user with credentials: email='$email', password='$password'")
        unsafeDatabaseUsage {
            UserRepository.save(
                User(
                    id = "inspector01",
                    email = email,
                    password = BCrypt.withDefaults().hashToString(10, password.toCharArray()),
                    organization = Organization.UrFU,
                    roles = arrayOf(Role.INSPECTOR),
                    isCredentialsVerified = true
                )
            )
        }
    }
}