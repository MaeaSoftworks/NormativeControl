package api.teachers.controllers

import api.common.components.AccessTokenService
import api.common.exceptions.*
import api.common.implementations.UserIdAuthentication
import api.common.repositories.RefreshTokenRepository
import api.common.repositories.UsersRepository
import api.common.services.AuthenticationManagerService
import api.common.services.RefreshTokenService
import api.teachers.dto.*
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

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
    fun login(@RequestBody @Valid credentials: LoginRequest): Mono<LoginResponse> {
        return usersRepository
            .getByUsername(credentials.username)
            .flatMap { authenticationManagerService.authenticate(UserIdAuthentication(it.id!!, password = credentials.password)) }
            .map {
                ReactiveSecurityContextHolder.withAuthentication(it)
                it as UserIdAuthentication
            }
            .flatMap { refreshTokenService.createRefreshToken(it.userId) }
            .map { LoginResponse(accessTokenService.generateToken(it.userId!!), it.value) }
            .switchIfEmpty { throw InvalidCredentialsException("User not found") }
    }

    @PatchMapping("/token", produces = ["application/json"])
    fun updateAccessToken(@Valid @NotBlank @RequestParam("refreshToken") refreshToken: String): Mono<UpdateAccessTokenResponse> {
        return refreshTokenService
            .findByToken(refreshToken)
            .switchIfEmpty { throw NotFoundException("Refresh token not found") }
            .handle { it, sink ->
                if (refreshTokenService.isNotExpired(it)) {
                    sink.next(it)
                }
            }
            .switchIfEmpty {
                Mono
                    .just(refreshToken)
                    .flatMap { tokenRepository.deleteByValue(it) }
                    .handle { _, sink -> sink.error(RefreshTokenExpiredException()) }
            }
            .map { UpdateAccessTokenResponse(accessTokenService.generateToken(it.userId!!)) }
    }

    @PatchMapping("/password")
    fun changePassword(@RequestBody passwordRequest: PasswordRequest): Mono<ResponsePayload> {
        return ReactiveSecurityContextHolder
            .getContext()
            .flatMap { usersRepository.getById((it.authentication as UserIdAuthentication).userId) }
            .handle { it, sink ->
                if (passwordEncoder.matches(passwordRequest.password, it.password)) {
                    sink.error(CredentialIsAlreadyUsedException())
                } else {
                    sink.next(it)
                }
            }.flatMap {
                it.password = passwordEncoder.encode(passwordRequest.password)
                usersRepository.save(it)
            }
            .flatMap { tokenRepository.deleteByUserId(it.id!!) }
            .map { ResponsePayload(it.toString(), Status.UPDATED) }
    }

    @PatchMapping("/username")
    fun changeUsername(@RequestBody usernameRequest: UsernameRequest): Mono<ResponsePayload> {
        return usersRepository
            .existsByUsername(usernameRequest.username)
            .handle { it, sink ->
                if (it) {
                    sink.error(PrincipalIsAlreadyUsedException())
                } else {
                    sink.next(it)
                }
            }
            .flatMap { ReactiveSecurityContextHolder.getContext() }
            .flatMap { usersRepository.getById((it.authentication as UserIdAuthentication).userId) }
            .flatMap { usersRepository.save(it.apply { username = usernameRequest.username }) }
            .map { ResponsePayload(it.id.toString(), Status.UPDATED) }
    }
}