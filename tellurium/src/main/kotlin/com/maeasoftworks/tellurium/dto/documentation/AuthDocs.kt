package com.maeasoftworks.tellurium.dto.documentation

import com.maeasoftworks.tellurium.components.JwtUtils
import com.maeasoftworks.tellurium.controllers.AuthController
import com.maeasoftworks.tellurium.dto.documentation.annotations.BodyParam
import com.maeasoftworks.tellurium.dto.documentation.annotations.Documented
import com.maeasoftworks.tellurium.dto.documentation.annotations.PossibleResponse
import com.maeasoftworks.tellurium.dto.request.LoginRequest
import com.maeasoftworks.tellurium.dto.request.RegistrationRequest
import com.maeasoftworks.tellurium.dto.request.TokenRefreshRequest
import com.maeasoftworks.tellurium.dto.response.JwtResponse
import com.maeasoftworks.tellurium.dto.response.TokenRefreshResponse
import com.maeasoftworks.tellurium.repository.UserRepository
import com.maeasoftworks.tellurium.services.RefreshTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestBody

sealed class AuthDocs(
    a: AuthenticationManager,
    u: UserRepository,
    e: PasswordEncoder,
    j: JwtUtils,
    r: RefreshTokenService
) : AuthController(a, u, e, j, r) {
    @Documented("docs.method.register.info")
    @PossibleResponse(HttpStatus.OK, description = "docs.method.register.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.register.response1")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.register.response2")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    abstract override fun registerUser(
        @Documented("docs.method.register.arg0")
        @BodyParam
        @RequestBody
        registrationRequest: RegistrationRequest
    )

    @Documented("docs.method.login.info")
    @PossibleResponse(HttpStatus.OK, JwtResponse::class, description = "docs.method.login.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.401")
    abstract override fun authenticateUser(
        @Documented("docs.method.login.arg0")
        @RequestBody
        @BodyParam
        loginRequest: LoginRequest
    ): JwtResponse

    @Documented("docs.method.refresh.info")
    @PossibleResponse(HttpStatus.OK, JwtResponse::class, description = "docs.method.refresh.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.401")
    abstract override fun refreshToken(
        @Documented("docs.method.refresh.arg0")
        @RequestBody
        @BodyParam
        request: TokenRefreshRequest
    ): TokenRefreshResponse
}
