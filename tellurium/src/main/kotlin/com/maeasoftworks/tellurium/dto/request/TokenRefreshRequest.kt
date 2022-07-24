package com.maeasoftworks.tellurium.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.tellurium.documentation.Documentation
import javax.validation.constraints.NotBlank

@Documentation
data class TokenRefreshRequest(
    @get:JsonProperty(value = "refresh-token")
    @NotBlank
    val refreshToken: String
)
