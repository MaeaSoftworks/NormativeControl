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
import ru.maeasoftworks.normativecontrol.api.shared.modules.JWTService
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.shared.services.RefreshTokenService
import ru.maeasoftworks.normativecontrol.api.app.Controller
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InspectorAccountController @Inject constructor(
    private val jwtService: JWTService,
    private val inspectorAccountService: InspectorAccountService,
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository
) : Controller() {
    override fun Routing.registerRoutes() {
        route("/inspector") {
            route("/account") {
                post("/login") {
                    val loginRequest = call.receive<LoginRequest>()
                    val userAgent = call.request.headers["User-Agent"]
                    val user = inspectorAccountService.authenticate(loginRequest)
                    val jwt = jwtService.createJWTToken(user.id)
                    val refreshToken = refreshTokenService.createRefreshTokenAndSave(user.id, userAgent)
                    call.respond(LoginResponse(jwt, refreshToken.refreshToken))
                }
                patch("/token") {
                    val refreshToken = call.parameters["refreshToken"] ?: throw IllegalArgumentException("refreshToken must be not null")
                    val userAgent = call.request.headers["User-Agent"]
                    val token = refreshTokenService.updateJwtToken(refreshToken, userAgent)
                    val jwt = jwtService.createJWTToken(userRepository.getById(token.userId)!!.id)
                    call.respond(UpdateAccessTokenResponse(jwt, token.refreshToken))
                }
                authenticate("jwt") {
                    get("/sessions") {
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        call.respond(refreshTokenService.getAllRefreshTokensOfUser(userId).map { Session(it.userAgent, it.createdAt) })
                    }
                    patch("password") {
                        val newPassword = call.receive<PasswordRequest>()
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        inspectorAccountService.changePassword(userId, newPassword.password)
                    }
                    patch("username") {
                        val newUsername = call.receive<UsernameRequest>()
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        inspectorAccountService.changeUsername(userId, newUsername.username)
                    }
                }
            }
        }
    }
}