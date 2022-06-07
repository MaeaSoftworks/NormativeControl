package com.maeasoftworks.normativecontrol.documentation

import com.maeasoftworks.normativecontrol.controllers.AuthController
import com.maeasoftworks.normativecontrol.documentation.annotations.BodyParam
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PossibleResponse
import com.maeasoftworks.normativecontrol.dto.request.LoginRequest
import com.maeasoftworks.normativecontrol.dto.request.RegistrationRequest
import com.maeasoftworks.normativecontrol.dto.request.TokenRefreshRequest
import com.maeasoftworks.normativecontrol.dto.response.JwtResponse
import com.maeasoftworks.normativecontrol.dto.response.TokenRefreshResponse
import com.maeasoftworks.normativecontrol.repository.RoleRepository
import com.maeasoftworks.normativecontrol.repository.UserRepository
import com.maeasoftworks.normativecontrol.services.RefreshTokenService
import com.maeasoftworks.normativecontrol.utils.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestBody

sealed class AuthDocs(a: AuthenticationManager, u: UserRepository, r: RoleRepository, e: PasswordEncoder, j: JwtUtils, r1: RefreshTokenService) : AuthController(a, u, r, e, j, r1) {
    @Documentation("docs.method.register.info")
    @PossibleResponse(HttpStatus.OK, description = "docs.method.register.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.register.response1")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.register.response2")
    @PossibleResponse(HttpStatus.UNAUTHORIZED, description = "docs.method.common.response.401")
    abstract override fun registerUser(
        @Documentation("docs.method.register.arg0")
        @BodyParam
        @RequestBody
        registrationRequest: RegistrationRequest
    )

    @Documentation("docs.method.login.info")
    @PossibleResponse(HttpStatus.OK, JwtResponse::class, description = "docs.method.login.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.401")
    abstract override fun authenticateUser(
        @Documentation("docs.method.login.arg0")
        @RequestBody
        @BodyParam
        loginRequest: LoginRequest
    ): JwtResponse

    @Documentation("docs.method.refresh.info")
    @PossibleResponse(HttpStatus.OK, JwtResponse::class, description = "docs.method.refresh.response0")
    @PossibleResponse(HttpStatus.BAD_REQUEST, description = "docs.method.common.response.401")
    abstract override fun refreshToken(
        @Documentation("docs.method.refresh.arg0")
        @RequestBody
        @BodyParam
        request: TokenRefreshRequest
    ): TokenRefreshResponse
}
