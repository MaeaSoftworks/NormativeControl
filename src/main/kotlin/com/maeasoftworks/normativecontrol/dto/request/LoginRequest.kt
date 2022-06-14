package com.maeasoftworks.normativecontrol.dto.request

import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import javax.validation.constraints.NotBlank

@Documented("docs.entity.LoginRequest.info")
data class LoginRequest(
    @NotBlank
    @DocumentedProperty("docs.entity.LoginRequest.prop0")
    val email: String,
    @NotBlank
    @DocumentedProperty("docs.entity.LoginRequest.prop1")
    val password: String
)
