package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import ru.maeasoftworks.normativecontrol.api.app.web.dto.RegistrationRequest
import ru.maeasoftworks.normativecontrol.api.app.web.dto.UpdateEmailStudentRequest
import ru.maeasoftworks.normativecontrol.api.domain.Organization
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module

object Validation : Module {
    private val emailRegex = """^[\w\-.]+@[\w-]+\.+\w{2,4}$""".toRegex()

    override fun Application.module() {
        install(RequestValidation) {
            validate<RegistrationRequest> {
                emailValidation(it.email)
            }
            validate<UpdateEmailStudentRequest> {
                emailValidation(it.email)
            }
        }
    }

    private fun emailValidation(email: String) =
        if (email.matches(emailRegex)) {
            if (
                Organization.domainRegex
                    .findAll(email)
                    .map { match -> match.groups.last()?.value }
                    .any { domain -> domain in Organization.allDomains }
            ) {
                ValidationResult.Valid
            } else {
                ValidationResult.Invalid("Email domain isn't supported yet")
            }
        } else {
            ValidationResult.Invalid("Value of 'email' does not matches email pattern")
        }
}