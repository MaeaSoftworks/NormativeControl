package ru.maeasoftworks.normativecontrol.api.app.web.controllers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.map
import ru.maeasoftworks.normativecontrol.api.app.web.dto.*
import ru.maeasoftworks.normativecontrol.api.domain.services.AccountService
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Security
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule

object AccountController : ControllerModule() {
    override fun Routing.register() {
        route("/account") {
            post("/register") {
                val registrationRequest = call.receive<RegistrationRequest>()

                val userAgent = call.request.headers["User-Agent"]
                val user = transaction { AccountService.register(registrationRequest) }
                val (jwt, ref) = Security.createTokenPair(user, userAgent)
                call.respond(CredentialsResponse(jwt, ref.asResponse(), user.isCredentialsVerified, user.roles))
            }

            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                val userAgent = call.request.headers["User-Agent"]
                val user = transaction { AccountService.authenticate(loginRequest) }
                val (jwt, ref) = Security.createTokenPair(user, userAgent)
                call.respond(CredentialsResponse(jwt, ref.asResponse(), user.isCredentialsVerified, user.roles))
            }

            patch("/token") {
                val refreshToken = call.parameters["refreshToken"] ?: throw IllegalArgumentException("refreshToken must be not null")
                val userAgent = call.request.headers["User-Agent"]
                val token = Security.RefreshTokens.updateJwtToken(refreshToken, userAgent)
                val jwt = Security.JWT.createJWTToken(transaction { UserRepository.identify(token.userId) })
                call.respond(CredentialsResponse(jwt, token.asResponse()))
            }

            authenticate(Security.JWT.CONFIGURATION_NAME) {
                get("/verify") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!
                    val instants = transaction { AccountService.createVerificationCode(userId) }
                    call.respond(AccountVerificationResponse(instants.first, instants.second))
                }

                post("/verify") {
                    val verificationRequest = call.receive<AccountVerificationRequest>()
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!
                    if (transaction { AccountService.verify(userId, verificationRequest.verificationCode) }) {
                        call.respond(HttpStatusCode.OK, "Successfully verified!")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Incorrect verification key")
                    }
                }

                get("/sessions") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!
                    call.respond(transaction { RefreshTokenRepository.getAllRefreshTokensOfUser(userId).map { Session(it.userAgent, it.createdAt) } })
                }

                patch("password") {
                    val newPassword = call.receive<PasswordRequest>()
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!
                    transaction { AccountService.changePassword(userId, newPassword.password) }
                }

                patch("email") {
                    val (userId, roles) = call.authentication.principal<JWTPrincipal>()!!
                        .let {it.subject!! to it.payload.getClaim("roles").asArray(Role::class.java) }
                    val newEmail = if (roles.any { it == Role.STUDENT }) {
                        call.receive<UpdateEmailStudentRequest>().email
                    } else {
                        call.receive<UpdateEmailRequest>().email
                    }
                    transaction { AccountService.changeEmail(userId, newEmail) }
                }
            }
        }
    }
}