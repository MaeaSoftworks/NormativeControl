package com.maeasoftworks.tellurium.advice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException

@ControllerAdvice
class FileSizeExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleFileSizeException(e: MaxUploadSizeExceededException?) =
        mapOf("status" to 400, "templates/error" to "Document was too big")
}
