package com.maeasoftworks.normativecontrol.dtos.docs

import org.springframework.http.HttpStatus

class Response(val httpStatus: HttpStatus,
               val type: String? = null,
               val description: String? = null,
               val body: String? = null) {
    fun getIntStatus(): Int {
        return httpStatus.value()
    }
}