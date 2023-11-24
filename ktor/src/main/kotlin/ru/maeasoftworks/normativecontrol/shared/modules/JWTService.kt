package ru.maeasoftworks.normativecontrol.shared.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.time.Instant
import java.util.*

class JWTService(application: Application) {
    private val jwtAudience = application.environment.config.property("jwt.audience").getString()
    private val issuer = application.environment.config.property("jwt.issuer").getString()
    private val jwtRealm = application.environment.config.property("jwt.realm").getString()
    private val jwtSecret = application.environment.config.property("jwt.secret").getString()
    private val jwtExpiration = application.environment.config.property("jwt.expiration").getString().toLong()

    init {
        application.authentication {
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
                    } else null
                }
            }
        }
    }

    fun createJWTToken(userId: Long): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}

