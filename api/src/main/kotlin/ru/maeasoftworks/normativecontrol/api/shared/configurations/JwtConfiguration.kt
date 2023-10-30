package ru.maeasoftworks.normativecontrol.api.shared.configurations

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
class JwtConfiguration {
    @Value("\${security.jwt.secret}")
    private lateinit var jwtSecret: String

    @Bean
    fun jwtSecret(): SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
}
