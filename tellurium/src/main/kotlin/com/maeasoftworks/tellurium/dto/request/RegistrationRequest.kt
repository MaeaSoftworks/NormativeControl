package com.maeasoftworks.tellurium.dto.request

import com.maeasoftworks.tellurium.documentation.Documentation
import javax.validation.constraints.Email

@Documentation
data class RegistrationRequest(
    val username: String,
    @Email
    val email: String,
    val password: String,
    val roles: Set<String>
)
