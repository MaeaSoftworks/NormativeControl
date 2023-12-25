package ru.maeasoftworks.normativecontrol.api.inspectors.controllers

import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.map
import ru.maeasoftworks.normativecontrol.api.inspectors.dto.*
import ru.maeasoftworks.normativecontrol.api.inspectors.services.InspectorAccountService
import ru.maeasoftworks.normativecontrol.api.shared.modules.JWT
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.shared.modules.RefreshTokenService
import ru.maeasoftworks.normativecontrol.api.shared.utils.ControllerModule

object InspectorAccountController: ControllerModule() {
    override fun Routing.register() {
        route("/inspector") {
            route("/account") {
                post("/login") {
                    val loginRequest = call.receive<LoginRequest>()
                    val userAgent = call.request.headers["User-Agent"]
                    val user = InspectorAccountService.authenticate(loginRequest)
                    val jwt = JWT.createJWTToken(user.id)
                    val refreshToken = RefreshTokenService.createRefreshTokenAndSave(user.id, userAgent)
                    call.respond(LoginResponse(jwt, refreshToken.refreshToken))
                }
                patch("/token") {
                    val refreshToken = call.parameters["refreshToken"] ?: throw IllegalArgumentException("refreshToken must be not null")
                    val userAgent = call.request.headers["User-Agent"]
                    val token = RefreshTokenService.updateJwtToken(refreshToken, userAgent)
                    val jwt = JWT.createJWTToken(UserRepository.getById(token.userId)!!.id)
                    call.respond(UpdateAccessTokenResponse(jwt, token.refreshToken))
                }
                authenticate("jwt") {
                    get("/sessions") {
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        call.respond(RefreshTokenService.getAllRefreshTokensOfUser(userId).map { Session(it.userAgent, it.createdAt) })
                    }
                    patch("password") {
                        val newPassword = call.receive<PasswordRequest>()
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        InspectorAccountService.changePassword(userId, newPassword.password)
                    }
                    patch("username") {
                        val newUsername = call.receive<UsernameRequest>()
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        InspectorAccountService.changeUsername(userId, newUsername.username)
                    }
                }
            }
        }
    }
}