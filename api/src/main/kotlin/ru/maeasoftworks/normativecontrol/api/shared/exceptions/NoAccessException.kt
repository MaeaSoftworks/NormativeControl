package ru.maeasoftworks.normativecontrol.api.shared.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class NoAccessException(message: String) : Exception(message)
