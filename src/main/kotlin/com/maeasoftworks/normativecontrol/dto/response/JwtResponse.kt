package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

class JwtResponse(
    @field:JsonProperty(value = "access-token")
    val accessToken: String,
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,
    val id: Long,
    private val username: String,
    val email: String?,
    private val roles: List<String>,
    @field:JsonProperty(value = "token-type")
    private val tokenType: String = "Bearer"
)