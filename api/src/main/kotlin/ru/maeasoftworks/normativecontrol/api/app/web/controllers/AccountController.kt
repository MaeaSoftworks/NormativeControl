package ru.maeasoftworks.normativecontrol.api.app.web.controllers

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
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.JwtRefreshToken
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Jwt
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.ControllerModule

object AccountController: ControllerModule() {
    override fun Routing.register() {
        route("/account") {
            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                val userAgent = call.request.headers["User-Agent"]
                val user = AccountService.authenticate(loginRequest)
                val jwt = Jwt.createJWTToken(user.id)
                val refreshToken = JwtRefreshToken.createRefreshTokenAndSave(user.id, userAgent)
                call.respond(LoginResponse(jwt, refreshToken.refreshToken))
            }
            patch("/token") {
                val refreshToken = call.parameters["refreshToken"] ?: throw IllegalArgumentException("refreshToken must be not null")
                val userAgent = call.request.headers["User-Agent"]
                val token = JwtRefreshToken.updateJwtToken(refreshToken, userAgent)
                val jwt = Jwt.createJWTToken(UserRepository.getById(token.userId)!!.id)
                call.respond(UpdateAccessTokenResponse(jwt, token.refreshToken))
            }
            authenticate(Jwt.CONFIGURATION_NAME) {
                get("/sessions") {
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                    call.respond(JwtRefreshToken.getAllRefreshTokensOfUser(userId).map { Session(it.userAgent, it.createdAt) })
                }
                patch("password") {
                    val newPassword = call.receive<PasswordRequest>()
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                    AccountService.changePassword(userId, newPassword.password)
                }
                patch("username") {
                    val newUsername = call.receive<UsernameRequest>()
                    val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                    AccountService.changeUsername(userId, newUsername.username)
                }
            }
        }
    }
}