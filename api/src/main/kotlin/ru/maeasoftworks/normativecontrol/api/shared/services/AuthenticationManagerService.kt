package ru.maeasoftworks.normativecontrol.api.shared.services

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.InvalidCredentialsException
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.UnauthorizedException
import ru.maeasoftworks.normativecontrol.api.shared.implementations.UserDetailsImpl
import ru.maeasoftworks.normativecontrol.api.shared.implementations.UserIdAuthentication

@Service
class AuthenticationManagerService(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication == null) {
            return Mono.error { UnauthorizedException("") }
        }
        authentication as UserIdAuthentication
        return userDetailsService
            .findByUsername(authentication.userId.toString())
            .handle { userDetails, sink ->
                userDetails as UserDetailsImpl
                if (authentication.password != null && !passwordEncoder.matches(authentication.password, userDetails.password)) {
                    sink.error(InvalidCredentialsException("Incorrect login or password"))
                } else {
                    authentication.authoritiesKt += SimpleGrantedAuthority("USER")
                    authentication.isAuthenticated = true
                    sink.next(authentication)
                }
            }
    }
}
