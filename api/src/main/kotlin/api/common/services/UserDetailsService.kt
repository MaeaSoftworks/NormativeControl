package api.common.services

import api.common.implementations.UserDetailsImpl
import api.common.repositories.UsersRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDetailsService(private val usersRepository: UsersRepository) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        return usersRepository
            .findById(username.toLong())
            .map { UserDetailsImpl(it.id!!, it.username, it.password) }
    }
}