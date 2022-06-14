package com.maeasoftworks.normativecontrol.dto.request

import com.maeasoftworks.normativecontrol.dto.documentation.annotations.Documented
import com.maeasoftworks.normativecontrol.dto.documentation.annotations.DocumentedProperty
import javax.validation.constraints.Email

@Documented("docs.entity.RegistrationRequest.info")
data class RegistrationRequest(
    @DocumentedProperty("docs.entity.RegistrationRequest.prop0")
    val username: String,
    @Email
    @DocumentedProperty("docs.entity.RegistrationRequest.prop1")
    val email: String,
    @DocumentedProperty("docs.entity.RegistrationRequest.prop2")
    val password: String,
    @DocumentedProperty("docs.entity.RegistrationRequest.prop3")
    val roles: Set<String>
)
