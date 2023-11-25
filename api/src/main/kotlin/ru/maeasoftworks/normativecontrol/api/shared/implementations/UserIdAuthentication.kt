package ru.maeasoftworks.normativecontrol.api.shared.implementations

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class UserIdAuthentication(
    val userId: Long,
    var password: String? = null,
    var token: String? = null
) : AbstractAuthenticationToken(null) {
    val authoritiesKt = mutableListOf<GrantedAuthority>()

    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        return this.authoritiesKt
    }

    // do not remove or modify for spring compatibility
    override fun getCredentials(): String {
        return password ?: token!!
    }

    // do not remove or modify for spring compatibility
    override fun getPrincipal(): Long {
        return userId
    }
}
