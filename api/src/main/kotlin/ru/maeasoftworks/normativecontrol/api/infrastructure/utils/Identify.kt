package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.IdentificationException

/**
 * Identifies user by JWTPrincipal
 * @return [User] if user exists
 * @throws IdentificationException if user was not found
 */
context(Transaction)
suspend fun AuthenticationContext.identify(): User {
    return UserRepository.identify(call.authentication.principal<JWTPrincipal>()!!.subject!!)
}
