package com.maeasoftworks.tellurium.services

import com.maeasoftworks.tellurium.dto.UserDetailsImpl
import com.maeasoftworks.tellurium.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(var userRepository: UserRepository) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
        if (user.isEmpty) {
            throw UsernameNotFoundException("User not found")
        } else {
            return UserDetailsImpl.build(user.get())
        }
    }

    fun loadUserByEmail(email: String): UserDetails = loadUserByUsername(email)
}
