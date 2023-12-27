package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.http.HttpStatusCode

open class StatusException(val code: HttpStatusCode, override val message: String) : Throwable(message)

class NoAccessException : StatusException(HttpStatusCode.Forbidden, "You don't have access to this document")
class AuthenticationException : StatusException(HttpStatusCode.Unauthorized, "Wrong credentials")
class OutdatedException(subject: String) : StatusException(HttpStatusCode.BadRequest, "$subject is outdated")
class InvalidRefreshToken : StatusException(HttpStatusCode.BadRequest, "RefreshToken is incorrect")
class CredentialsIsAlreadyInUseException : StatusException(HttpStatusCode.Conflict, "Credentials is already in use")
class EntityNotFoundException(entityName: String) : StatusException(HttpStatusCode.NotFound, "$entityName not found")
class InconsistentStateException(message: String? = null) : StatusException(HttpStatusCode.Conflict, message ?: "Inconsistent state")