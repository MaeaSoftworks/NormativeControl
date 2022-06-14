package com.maeasoftworks.normativecontrol.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty

@Documented("docs.entity.JwtResponse.info")
data class JwtResponse(
    @DocumentedProperty("docs.entity.JwtResponse.prop0")
    @field:JsonProperty(value = "access-token")
    val accessToken: String,

    @DocumentedProperty("docs.entity.JwtResponse.prop1")
    @field:JsonProperty(value = "refresh-token")
    val refreshToken: String,

    @field:JsonIgnore
    val id: Long,

    @DocumentedProperty("docs.entity.JwtResponse.prop2")
    val username: String,

    @DocumentedProperty("docs.entity.JwtResponse.prop3")
    val email: String?,

    @DocumentedProperty("docs.entity.JwtResponse.prop4")
    val roles: List<String>,

    @DocumentedProperty("docs.entity.JwtResponse.prop5")
    @field:JsonProperty(value = "token-type")
    val tokenType: String = "Bearer"
)
