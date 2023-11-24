package ru.maeasoftworks.normativecontrol.inspectors.controllers

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance
import ru.maeasoftworks.normativecontrol.inspectors.dto.LoginRequest
import ru.maeasoftworks.normativecontrol.inspectors.dto.LoginResponse
import ru.maeasoftworks.normativecontrol.inspectors.dto.SessionsResponse
import ru.maeasoftworks.normativecontrol.inspectors.dto.UpdateAccessTokenResponse
import ru.maeasoftworks.normativecontrol.inspectors.services.InspectorAccountService
import ru.maeasoftworks.normativecontrol.shared.modules.JWTService
import ru.maeasoftworks.normativecontrol.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.shared.services.RefreshTokenService
import ru.maeasoftworks.normativecontrol.shared.utils.Controller

class InspectorViewController(override val di: DI): Controller() {
    private val jwtService: JWTService by instance()
    private val inspectorAccountService: InspectorAccountService by instance()
    private val refreshTokenService: RefreshTokenService by instance()
    private val userRepository: UserRepository by instance()

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
                    val jwt = jwtService.createJWTToken(userRepository.getUserById(token.userId)!!.id)
                    call.respond(UpdateAccessTokenResponse(jwt, token.refreshToken))
                }
                authenticate("jwt") {
                    get("/sessions") {
                        val userId = call.authentication.principal<JWTPrincipal>()!!.subject!!.toLong()
                        call.respond(
                            SessionsResponse(
                                userRepository.getUserAllRefreshTokens(userId).map { SessionsResponse.Session(it.userAgent, it.createdAt) }
                            )
                        )
                    }
                    patch("password") {

                    }
                    patch("username") {

                    }
                }
            }

            authenticate("jwt") {
                route("/document") {
                    get("/render") {

                    }
                    get("/conclusion") {

                    }
                }
            }
        }
    }
}