package ru.maeasoftworks.normativecontrol.api.shared.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class CredentialIsAlreadyUsedException : Exception("Credential is already used")
