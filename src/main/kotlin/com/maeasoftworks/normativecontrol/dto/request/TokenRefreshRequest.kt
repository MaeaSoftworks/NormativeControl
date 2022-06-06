package com.maeasoftworks.normativecontrol.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @field:JsonProperty(value = "refresh-token")
    @NotBlank
    val refreshToken: String
)