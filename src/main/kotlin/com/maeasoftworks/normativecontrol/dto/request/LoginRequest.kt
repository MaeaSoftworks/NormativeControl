package com.maeasoftworks.normativecontrol.dto.request

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import javax.validation.constraints.NotBlank

@Documentation("docs.entity.LoginRequest.info")
data class LoginRequest(
    @NotBlank
    @PropertyDocumentation("docs.entity.LoginRequest.prop0")
    val email: String,
    @NotBlank
    @PropertyDocumentation("docs.entity.LoginRequest.prop1")
    val password: String
)
