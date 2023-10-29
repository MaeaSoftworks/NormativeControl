package api.common.components

import api.common.services.AuthenticationManagerService
import api.common.implementations.UserIdAuthentication
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtFilter(
    private val accessTokenService: AccessTokenService,
    private val authenticationManagerService: AuthenticationManagerService
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val header = exchange.request.headers["Authorization"]
        var authentication: Authentication? = null
        if (!header.isNullOrEmpty()) {
            val token = if (StringUtils.hasText(header[0]) && header[0].startsWith("Bearer "))
                header[0].removePrefix("Bearer ")
            else
                throw AuthenticationCredentialsNotFoundException("Request does not contain Authorization header")
            val userId = accessTokenService.validateJwtTokenAndGetUserId(token)
            authentication = UserIdAuthentication(userId, token = token)
        }
        return chain
            .filter(exchange)
            .contextWrite(
                ReactiveSecurityContextHolder.withSecurityContext(
                    authenticationManagerService.authenticate(authentication).map { SecurityContextImpl(it) }
                )
            )
    }
}
