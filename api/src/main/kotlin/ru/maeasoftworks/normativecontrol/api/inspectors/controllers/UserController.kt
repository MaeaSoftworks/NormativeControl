package ru.maeasoftworks.normativecontrol.api.inspectors.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.maeasoftworks.normativecontrol.api.inspectors.dto.*
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.*
import ru.maeasoftworks.normativecontrol.api.shared.implementations.UserIdAuthentication
import ru.maeasoftworks.normativecontrol.api.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UsersRepository
import ru.maeasoftworks.normativecontrol.api.shared.services.AccessTokenService
import ru.maeasoftworks.normativecontrol.api.shared.services.AuthenticationManagerService
import ru.maeasoftworks.normativecontrol.api.shared.services.RefreshTokenService

@FlowPreview
@RestController
@RequestMapping("/teacher/account")
@CrossOrigin
class UserController(
    private val usersRepository: UsersRepository,
    private val tokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManagerService: AuthenticationManagerService,
    private val accessTokenService: AccessTokenService,
    private val refreshTokenService: RefreshTokenService
) {
    @PostMapping("/login", produces = ["application/json"])
    fun login(
        @RequestBody @Valid
        credentials: LoginRequest
    ): Flow<LoginResponse> {
        return usersRepository.getByUsername(credentials.username)
            .flatMapConcat {
                authenticationManagerService.authenticate(UserIdAuthentication(it.id!!, password = credentials.password)).asFlow()
            }.map {
                it as UserIdAuthentication
                ReactiveSecurityContextHolder.withAuthentication(it)
                val token = refreshTokenService.createRefreshToken(it.userId)
                LoginResponse(accessTokenService.generateToken(it.userId), token.value)
            }.onEmpty {
                throw InvalidCredentialsException("User not found")
            }
    }

    @PatchMapping("/token", produces = ["application/json"])
    fun updateAccessToken(
        @Valid
        @NotBlank
        @RequestParam("refreshToken")
        refreshToken: String
    ): Flow<UpdateAccessTokenResponse> {
        return refreshTokenService.findByToken(refreshToken)
            .onEmpty {
                throw NotFoundException("Refresh token not found")
            }.map {
                if (refreshTokenService.isNotExpired(it)) {
                    tokenRepository.delete(it)
                    throw RefreshTokenExpiredException()
                }
                UpdateAccessTokenResponse(accessTokenService.generateToken(it.userId!!))
            }
    }

    @PatchMapping("/password")
    fun changePassword(@RequestBody passwordRequest: PasswordRequest): Flow<ResponsePayload> {
        return ReactiveSecurityContextHolder
            .getContext()
            .asFlow()
            .map {
                usersRepository.findById((it.authentication as UserIdAuthentication).userId)
            }
            .filter {
                passwordEncoder.matches(passwordRequest.password, it.password)
            }
            .onEmpty { throw CredentialIsAlreadyUsedException() }
            .map {
                it.password = passwordEncoder.encode(passwordRequest.password)
                usersRepository.save(it)
                tokenRepository.deleteByUserId(it.id!!)
                ResponsePayload(it.toString(), Status.UPDATED)
            }
    }

    @PatchMapping("/username")
    fun changeUsername(@RequestBody usernameRequest: UsernameRequest): Flow<ResponsePayload> {
        return usersRepository
            .existsByUsername(usernameRequest.username)
            .map {
                if (it) throw PrincipalIsAlreadyUsedException() else false
            }
            .flatMapConcat {
                ReactiveSecurityContextHolder.getContext().asFlow()
            }
            .map {
                usersRepository.findById((it.authentication as UserIdAuthentication).userId)
            }
            .map {
                usersRepository.save(it.apply { username = usernameRequest.username })
                ResponsePayload(it.id.toString(), Status.UPDATED)
            }
    }
}
