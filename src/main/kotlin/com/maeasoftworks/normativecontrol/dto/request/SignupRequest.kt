package com.maeasoftworks.normativecontrol.dto.request

data class SignupRequest(
    val username: String,
    val email: String,
    val password: String,
    val roles: Set<String>
)