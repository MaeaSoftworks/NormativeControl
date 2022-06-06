package com.maeasoftworks.normativecontrol.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import javax.validation.constraints.NotBlank

@Documentation("docs.entity.TokenRefreshRequest.info")
data class TokenRefreshRequest(
    @field:JsonProperty(value = "refresh-token")
    @NotBlank
    @PropertyDocumentation("docs.entity.TokenRefreshRequest.prop0")
    val refreshToken: String
)