package com.maeasoftworks.normativecontrol.controllers

import com.maeasoftworks.normativecontrol.dao.RefreshToken
import com.maeasoftworks.normativecontrol.dao.Role
import com.maeasoftworks.normativecontrol.dao.User
import com.maeasoftworks.normativecontrol.dto.RoleType
import com.maeasoftworks.normativecontrol.dto.UserDetailsImpl
import com.maeasoftworks.normativecontrol.dto.request.LoginRequest
import com.maeasoftworks.normativecontrol.dto.request.SignupRequest
import com.maeasoftworks.normativecontrol.dto.request.TokenRefreshRequest
import com.maeasoftworks.normativecontrol.dto.response.JwtResponse
import com.maeasoftworks.normativecontrol.dto.response.TokenRefreshResponse
import com.maeasoftworks.normativecontrol.repository.RoleRepository
import com.maeasoftworks.normativecontrol.repository.UserRepository
import com.maeasoftworks.normativecontrol.services.RefreshTokenService
import com.maeasoftworks.normativecontrol.utils.JwtUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.function.Consumer
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
    @PostMapping("/login")
    @ResponseBody
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): JwtResponse {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        val userDetails = authentication.principal as UserDetailsImpl
        val jwt = jwtUtils.generateJwtToken(authentication)
        val roles: List<String> = userDetails.authorities
            .map { item: GrantedAuthority -> item.authority }
        val refreshToken: RefreshToken = refreshTokenService.createRefreshToken(userDetails.id)
        return JwtResponse(
            jwt,
            refreshToken.token,
            userDetails.id,
            userDetails.username,
            userDetails.email,
            roles
        )
    }

    @PostMapping("/refresh-token")
    @ResponseBody
    fun refreshToken(@RequestBody request: @Valid TokenRefreshRequest): TokenRefreshResponse {
        val requestRefreshToken = request.refreshToken
        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::user)
            .map { user: User ->
                val token = jwtUtils.generateTokenFromUsername(user.username)
                TokenRefreshResponse(token!!, requestRefreshToken)
            }.orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token not found!") }
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('DEV')")
    @ResponseBody
    fun registerUser(@Valid @RequestBody signUpRequest: SignupRequest): String {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Username is already taken!")
        }
        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!")
        }
        // Create new user's account
        val user = User(
            signUpRequest.username,
            signUpRequest.email,
            encoder.encode(signUpRequest.password)
        )
        val strRoles: Set<String> = signUpRequest.roles
        val roles: MutableSet<Role> = HashSet()
        strRoles.forEach(Consumer { role: String? ->
            when (role) {
                "admin" -> {
                    val adminRole: Role = roleRepository.findByName(RoleType.ROLE_ADMIN)
                        .orElseThrow {
                            throw ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Error: Role is not found."
                            )
                        }
                    roles.add(adminRole)
                }
                "dev" -> {
                    val modRole: Role = roleRepository.findByName(RoleType.ROLE_DEV)
                        .orElseThrow {
                            throw ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Error: Role is not found."
                            )
                        }
                    roles.add(modRole)
                }
            }
        })
        user.roles = roles
        userRepository.save<User>(user)
        return "User registered successfully!"
    }
}