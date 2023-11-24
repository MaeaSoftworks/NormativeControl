package ru.maeasoftworks.normativecontrol.shared.exceptions

import io.ktor.http.*

open class StatusException(val code: HttpStatusCode, override val message: String): Throwable(message)

class IncorrectFileException(message: String) : Exception(message)
class NoAccessException: StatusException(HttpStatusCode.Forbidden, "You don't have access to this document")
class AuthenticationException: StatusException(HttpStatusCode.Unauthorized, "Wrong credentials")
class OutdatedRefreshToken: StatusException(HttpStatusCode.Unauthorized, "RefreshToken is outdated")
class InvalidRefreshToken: StatusException(HttpStatusCode.BadRequest, "RefreshToken is incorrect")