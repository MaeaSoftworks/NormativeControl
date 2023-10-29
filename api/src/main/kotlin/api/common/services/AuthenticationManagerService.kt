package api.common.services

import api.common.exceptions.InvalidCredentialsException
import api.common.exceptions.UnauthorizedException
import api.common.implementations.UserDetailsImpl
import api.common.implementations.UserIdAuthentication
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

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