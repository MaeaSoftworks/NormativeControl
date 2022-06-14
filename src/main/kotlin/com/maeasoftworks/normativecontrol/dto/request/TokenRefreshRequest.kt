package com.maeasoftworks.normativecontrol.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import javax.validation.constraints.NotBlank

@Documented("docs.entity.TokenRefreshRequest.info")
data class TokenRefreshRequest(
    @field:JsonProperty(value = "refresh-token")
    @NotBlank
    @DocumentedProperty("docs.entity.TokenRefreshRequest.prop0")
    val refreshToken: String
)
