package api.common.components

import io.jsonwebtoken.*
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.stereotype.Service
import java.security.Key
import java.security.SignatureException
import java.util.*

@Service
class AccessTokenService(private val jwtSecret: Key) {
    @Value("\${security.jwt.expirationMs}")
    private var jwtExpirationMs: Long = 0
    private lateinit var parser: JwtParser

    @PostConstruct
    private fun initParser() {
        parser = Jwts.parserBuilder().setSigningKey(jwtSecret).build()
    }

    fun generateToken(userId: Long): String = Date().run {
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(this)
            .setExpiration(Date(time + jwtExpirationMs))
            .signWith(jwtSecret, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateJwtTokenAndGetUserId(authToken: String?): Long {
        try {
            return parser.parseClaimsJws(authToken).body.subject.toLong()
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException -> throw CredentialsExpiredException("Access token is expired")
                is MalformedJwtException -> throw BadCredentialsException("Access token was invalid")
                is UnsupportedJwtException -> throw InternalAuthenticationServiceException("Access token does not represent a Claims JWS")
                is IllegalArgumentException -> throw BadCredentialsException("Access token was empty")
                is SignatureException -> throw BadCredentialsException("Access token was invalid")
                else -> throw e
            }
        }
    }
}
