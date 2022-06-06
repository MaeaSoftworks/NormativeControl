package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenRefreshResponse(
    @field:JsonProperty(value = "access-token")
    val accessToken: String,
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,
    @field:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)