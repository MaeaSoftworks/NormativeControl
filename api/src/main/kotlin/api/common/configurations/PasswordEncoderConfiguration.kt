package api.common.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class PasswordEncoderConfiguration {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}