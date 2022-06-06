package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.dao.User
import com.maeasoftworks.normativecontrol.dto.RoleType
import com.maeasoftworks.normativecontrol.dto.UserDetailsImpl
import com.maeasoftworks.normativecontrol.dto.request.LoginRequest
import com.maeasoftworks.normativecontrol.dto.request.RegistrationRequest
import com.maeasoftworks.normativecontrol.dto.request.TokenRefreshRequest
import com.maeasoftworks.normativecontrol.dto.response.JwtResponse
import com.maeasoftworks.normativecontrol.dto.response.TokenRefreshResponse
import com.maeasoftworks.normativecontrol.repository.RoleRepository
import com.maeasoftworks.normativecontrol.repository.UserRepository
import com.maeasoftworks.normativecontrol.services.RefreshTokenService
import com.maeasoftworks.normativecontrol.utils.JwtUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
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
@ConditionalOnExpression("\${controllers.api}")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val refreshTokenService: RefreshTokenService
) {
    @PostMapping("register")
    //@PreAuthorize("hasRole('DEV')")
    fun registerUser(@Valid @RequestBody registrationRequest: RegistrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!")
        }
        userRepository.save(
            User(registrationRequest.username,
                registrationRequest.email,
                encoder.encode(registrationRequest.password)
            ).also {
                it.roles = registrationRequest.roles.map{ role ->
                    roleRepository.findByName(name = try {
                        RoleType.valueOf("ROLE_${role.uppercase()}")
                    } catch (e: IllegalArgumentException) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is not found.")
                    }).get()
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
                refreshTokenService.createRefreshToken(it.id).token,
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