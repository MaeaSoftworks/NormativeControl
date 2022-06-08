package com.maeasoftworks.normativecontrol.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.maeasoftworks.normativecontrol.dao.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    val id: Long,
    private val username: String,
    val email: String,
    @field:JsonIgnore
    private val password: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        private const val serialVersionUID = 1L
        fun build(user: User) = UserDetailsImpl(
            user.id,
            user.username,
            user.email,
            user.password,
            user.roles.map { SimpleGrantedAuthority(it.name) }
        )
    }
}
