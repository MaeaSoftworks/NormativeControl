package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation

@Documentation("docs.entity.TokenRefreshResponse.info")
data class TokenRefreshResponse(
    @PropertyDocumentation("docs.entity.TokenRefreshResponse.prop0")
    @field:JsonProperty(value = "access-token")
    val accessToken: String,

    @PropertyDocumentation("docs.entity.TokenRefreshResponse.prop1")
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,

    @PropertyDocumentation("docs.entity.TokenRefreshResponse.prop2")
    @field:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)
