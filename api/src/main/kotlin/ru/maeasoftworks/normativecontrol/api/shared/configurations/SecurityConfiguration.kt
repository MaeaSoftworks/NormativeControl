package ru.maeasoftworks.normativecontrol.api.shared.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import ru.maeasoftworks.normativecontrol.api.shared.services.JwtFilter

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfiguration(
    private val jwtFilter: JwtFilter
) {
    @Bean
    fun commonApiSecurity(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.invoke {
            cors { }
            csrf { disable() }
            addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            authorizeExchange {
                authorize("/student/**", permitAll)
                authorize("/teacher/account/login", permitAll)
                authorize("/teacher/account/token", permitAll)
                authorize("/teacher/account/register", permitAll)
                authorize("/teacher/**", authenticated)
                authorize(anyExchange, authenticated)
            }
        }
    }
}
