package com.maeasoftworks.normativecontrol.advices

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException

@ControllerAdvice
internal class FileSizeExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleFileSizeException(e: MaxUploadSizeExceededException?): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["status"] = 400
        map["templates/error"] = "Document was too big"
        return map
    }
}