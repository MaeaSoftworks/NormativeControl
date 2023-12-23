package ru.maeasoftworks.normativecontrol.api.shared.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import java.time.Instant

object JWTService {
    private lateinit var jwtAudience: String
    private lateinit var issuer: String
    private lateinit var jwtRealm: String
    private lateinit var jwtSecret: String
    private var jwtExpiration: Long = 0

    fun Application.configureJWT() {
        jwtAudience = environment.config.property("jwt.audience").getString()
        issuer = environment.config.property("jwt.issuer").getString()
        jwtRealm = environment.config.property("jwt.realm").getString()
        jwtSecret = environment.config.property("jwt.secret").getString()
        jwtExpiration = environment.config.property("jwt.jwtTokenExpiration").getString().toLong()

        authentication {
            jwt("jwt") {
                realm = jwtRealm
                verifier(JWT.require(Algorithm.HMAC256(jwtSecret)).withAudience(jwtAudience).withIssuer(issuer).build())
                validate { credential ->
                    if (credential.payload.audience.size == 1 && credential.payload.audience[0] == jwtAudience &&
                        credential.payload.issuer == issuer &&
                        credential.payload.subject.isNotBlank() &&
                        credential.payload.expiresAtAsInstant >= Instant.now()
                    ) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun createJWTToken(userId: Long): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}