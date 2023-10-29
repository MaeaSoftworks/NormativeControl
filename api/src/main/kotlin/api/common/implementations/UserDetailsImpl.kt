package api.common.implementations

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsImpl(
    val userId: Long,
    @get:JvmName("_username")
    val username: String? = null,
    @get:JvmName("_password")
    val password: String
) : UserDetails {
    override fun getAuthorities() = Companion.authorities

    override fun getPassword() = password

    override fun getUsername() = userId.toString()

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true

    companion object {
        private val AUTHORITY = SimpleGrantedAuthority("USER")
        val authorities = mutableListOf(AUTHORITY)
    }
}