package ru.maeasoftworks.normativecontrol.api.shared.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.kodein.di.DI
import ru.maeasoftworks.normativecontrol.api.shared.utils.Service
import java.time.Instant

class JWTService(override val di: DI): Service() {
    private val jwtAudience = application.environment.config.property("jwt.audience").getString()
    private val issuer = application.environment.config.property("jwt.issuer").getString()
    private val jwtRealm = application.environment.config.property("jwt.realm").getString()
    private val jwtSecret = application.environment.config.property("jwt.secret").getString()
    private val jwtExpiration = application.environment.config.property("jwt.jwtTokenExpiration").getString().toLong()

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
            .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
            .sign(Algorithm.HMAC256(jwtSecret))
    }
}

