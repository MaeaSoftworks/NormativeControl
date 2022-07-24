package com.maeasoftworks.tellurium.dto.request

import com.maeasoftworks.tellurium.documentation.Documentation
import javax.validation.constraints.NotBlank

@Documentation
data class LoginRequest(
    @NotBlank
    val email: String,
    @NotBlank
    val password: String
)
