package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import ru.maeasoftworks.normativecontrol.api.app.web.dto.RegistrationRequest
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object Validation: Module {
    override fun Application.module() {
        install(RequestValidation) {
            validate<RegistrationRequest> {
                if (it.email.matches(Regex("""^[\w\-.]+@[\w-]+\.+\w{2,4}$"""))) {
                    ValidationResult.Valid
                }
                else {
                    ValidationResult.Invalid("Value of 'email' does not matches email pattern")
                }
            }
        }
    }
}