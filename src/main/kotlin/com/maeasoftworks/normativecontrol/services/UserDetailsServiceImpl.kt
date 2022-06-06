package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.dto.UserDetailsImpl
import com.maeasoftworks.normativecontrol.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserDetailsServiceImpl(var userRepository: UserRepository) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username).orElseThrow {
            UsernameNotFoundException("User Not Found with username: $username")
        }
        return UserDetailsImpl.build(user)
    }
}