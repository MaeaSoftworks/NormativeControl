package com.maeasoftworks.normativecontrol.dto.request

import javax.validation.constraints.Email

data class RegistrationRequest(
    val username: String,
    @Email
    val email: String,
    val password: String,
    val roles: Set<String>
)