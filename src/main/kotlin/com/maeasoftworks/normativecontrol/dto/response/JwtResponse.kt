package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation

@Documentation("docs.entity.JwtResponse.info")
data class JwtResponse(
    @PropertyDocumentation("docs.entity.JwtResponse.prop0")
    @field:JsonProperty(value = "access-token")
    val accessToken: String,

    @PropertyDocumentation("docs.entity.JwtResponse.prop1")
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,

    @field:JsonIgnore
    val id: Long,

    @PropertyDocumentation("docs.entity.JwtResponse.prop2")
    val username: String,

    @PropertyDocumentation("docs.entity.JwtResponse.prop3")
    val email: String?,

    @PropertyDocumentation("docs.entity.JwtResponse.prop4")
    val roles: List<String>,

    @PropertyDocumentation("docs.entity.JwtResponse.prop5")
    @field:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)