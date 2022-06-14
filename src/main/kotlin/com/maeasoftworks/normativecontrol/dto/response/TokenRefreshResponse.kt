package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty

@Documented("docs.entity.TokenRefreshResponse.info")
data class TokenRefreshResponse(
    @DocumentedProperty("docs.entity.TokenRefreshResponse.prop0")
    @field:JsonProperty(value = "access-token")
    val accessToken: String,

    @DocumentedProperty("docs.entity.TokenRefreshResponse.prop1")
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,

    @DocumentedProperty("docs.entity.TokenRefreshResponse.prop2")
    @field:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)
