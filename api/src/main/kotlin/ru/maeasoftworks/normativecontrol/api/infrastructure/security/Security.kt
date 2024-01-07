package ru.maeasoftworks.normativecontrol.api.infrastructure.security

import com.auth0.jwt.JWT as JWTLib
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import kotlinx.coroutines.flow.Flow
import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.domain.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InvalidRefreshToken
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.OutdatedException
import java.time.Instant

object Security: Module {
    override fun Application.module() {
        JWT.apply { module() }
        RefreshTokens.apply { module() }
    }

    suspend fun createTokenPair(userId: String, useragent: String?): Pair<String, RefreshToken> {
        val jwt = JWT.createJWTToken(userId)
        val refreshToken = RefreshTokens.createRefreshTokenAndSave(userId, useragent)
        return jwt to refreshToken
    }

    object JWT: Module {
        const val CONFIGURATION_NAME = "jwt"
        private lateinit var jwtAudience: String
        private lateinit var issuer: String
        private lateinit var jwtRealm: String

        private lateinit var jwtSecret: String

        private var jwtExpiration: Long = 0
        override fun Application.module() {
            jwtAudience = environment.config.property("security.jwt.audience").getString()
            issuer = environment.config.property("security.jwt.issuer").getString()
            jwtRealm = environment.config.property("security.jwt.realm").getString()
            jwtSecret = environment.config.property("security.jwt.secret").getString()
            jwtExpiration = environment.config.property("security.jwt.jwtTokenExpirationSeconds").getString().toLong()

            authentication {
                jwt(CONFIGURATION_NAME) {
                    realm = jwtRealm
                    verifier(JWTLib.require(Algorithm.HMAC256(jwtSecret)).withAudience(jwtAudience).withIssuer(issuer).build())
                    validate { credential ->
                        if (credential.payload.audience.size == 1 &&
                            credential.payload.audience[0] == jwtAudience &&
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
        fun createJWTToken(userId: String): String {
            return JWTLib.create()
                .withAudience(jwtAudience)
                .withIssuer(issuer)
                .withSubject(userId)
                .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
                .sign(Algorithm.HMAC256(jwtSecret))
        }
    }

    object RefreshTokens: Module {
        private var refreshTokenExpiration: Long = 0

        override fun Application.module() {
            refreshTokenExpiration = environment.config.property("security.jwt.refreshTokenExpirationSeconds").getString().toLong()
        }

        suspend fun updateJwtToken(refreshToken: String, userAgent: String?): RefreshToken {
            val token = RefreshTokenRepository.getRefreshTokenByValue(refreshToken)
            if (token != null) {
                RefreshTokenRepository.delete(token.id)
                if (token.expiresAt >= Instant.now()) {
                    return createRefreshTokenAndSave(token.userId, userAgent)
                }
                throw OutdatedException("Refresh token")
            }
            throw InvalidRefreshToken()
        }

        suspend fun createRefreshTokenAndSave(userId: String, userAgent: String?): RefreshToken {
            return RefreshTokenRepository.save(
                RefreshToken(
                    refreshToken = createRefreshTokenString(),
                    expiresAt = Instant.now().plusSeconds(refreshTokenExpiration),
                    userId = userId,
                    createdAt = Instant.now(),
                    userAgent = userAgent
                )
            )
        }

        private fun createRefreshTokenString(): String = KeyGenerator.generate(32)

        suspend fun getAllRefreshTokensOfUser(userId: String): Flow<RefreshToken> = RefreshTokenRepository.getAllBy(Meta.refreshTokens.userId, userId)
    }
}