package com.maeasoftworks.tellurium.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class JwtResponse(
    @get:JsonProperty(value = "access-token")
    val accessToken: String,

    @get:JsonProperty(value = "refresh-token")
    val refreshToken: String,

    @get:JsonIgnore
    val id: Long,

    val username: String,

    val email: String?,

    val roles: List<String>,

    @get:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)
