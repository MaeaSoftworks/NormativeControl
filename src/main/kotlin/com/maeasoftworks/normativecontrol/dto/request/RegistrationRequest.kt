package com.maeasoftworks.normativecontrol.dto.request

import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import javax.validation.constraints.Email

@Documentation("docs.entity.RegistrationRequest.info")
data class RegistrationRequest(
    @PropertyDocumentation("docs.entity.RegistrationRequest.prop0")
    val username: String,
    @Email
    @PropertyDocumentation("docs.entity.RegistrationRequest.prop1")
    val email: String,
    @PropertyDocumentation("docs.entity.RegistrationRequest.prop2")
    val password: String,
    @PropertyDocumentation("docs.entity.RegistrationRequest.prop3")
    val roles: Set<String>
)
