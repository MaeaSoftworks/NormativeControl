package com.maeasoftworks.tellurium.controllers

import com.maeasoftworks.tellurium.components.JwtUtils
import com.maeasoftworks.tellurium.dao.User
import com.maeasoftworks.tellurium.dto.RoleType
import com.maeasoftworks.tellurium.dto.UserDetailsImpl
import com.maeasoftworks.tellurium.documentation.Documentation
import com.maeasoftworks.tellurium.dto.request.LoginRequest
import com.maeasoftworks.tellurium.dto.request.RegistrationRequest
import com.maeasoftworks.tellurium.dto.request.TokenRefreshRequest
import com.maeasoftworks.tellurium.dto.response.JwtResponse
import com.maeasoftworks.tellurium.dto.response.TokenRefreshResponse
import com.maeasoftworks.tellurium.repository.UserRepository
import com.maeasoftworks.tellurium.services.RefreshTokenService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("auth")
@Documentation
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val refreshTokenService: RefreshTokenService
) {
    @PostMapping("register")
    @PreAuthorize("hasRole('DEV')")
    fun registerUser(@Valid @RequestBody registrationRequest: RegistrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!")
        }
        userRepository.save(
            User(
                registrationRequest.username,
                registrationRequest.email,
                encoder.encode(registrationRequest.password)
            ).also {
                it.roles = registrationRequest.roles.map { role ->
                    try {
                        RoleType.valueOf("ROLE_${role.uppercase()}")
                    } catch (e: IllegalArgumentException) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is not found.")
                    }
                }.toSet()
            }
        )
    }

    @PostMapping("login")
    @ResponseBody
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): JwtResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        return (authentication.principal as UserDetailsImpl).let {
            JwtResponse(
                jwtUtils.generateJwtToken(authentication),
                refreshTokenService.createRefreshToken(it.id).refreshToken,
                it.id,
                it.username,
                it.email,
                it.authorities.map { x -> x.authority }
            )
        }
    }

    @PostMapping("refresh-token")
    @ResponseBody
    fun refreshToken(@RequestBody request: @Valid TokenRefreshRequest) = request.refreshToken.let {
        TokenRefreshResponse(
            jwtUtils.generateTokenFromEmail(
                refreshTokenService.verifyExpiration(
                    refreshTokenService.findByToken(it)
                        .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token not found!") }
                ).user.email
            ),
            it
        )
    }
}
