package com.maeasoftworks.tellurium.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @get:JsonProperty(value = "refresh-token")
    @NotBlank
    val refreshToken: String
)
