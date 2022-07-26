package com.maeasoftworks.tellurium.configs

import com.maeasoftworks.tellurium.controllers.*
import com.maeasoftworks.tellurium.documentation.Controllers
import com.maeasoftworks.tellurium.documentation.Entities
import com.maeasoftworks.tellurium.dto.request.LoginRequest
import com.maeasoftworks.tellurium.dto.request.RegistrationRequest
import com.maeasoftworks.tellurium.dto.request.TokenRefreshRequest
import com.maeasoftworks.tellurium.dto.response.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DocumentationConfig {
    @Bean
    fun getControllers(): Controllers {
        return Controllers(
            AuthController::class,
            ControlPanelController::class,
            DocumentController::class,
            QueueController::class,
            DocumentationController::class
        )
    }

    @Bean
    fun getEntities(): Entities {
        return Entities(
            LoginRequest::class,
            RegistrationRequest::class,
            TokenRefreshRequest::class,
            DocumentControlPanelResponse::class,
            JwtResponse::class,
            Mistake::class,
            MistakesResponse::class,
            QueueResponse::class,
            StatusResponse::class,
            TokenRefreshResponse::class
        )
    }
}