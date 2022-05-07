package com.maeasoftworks.normativecontrol.dtos.docs

import org.springframework.http.HttpStatus

class Response(
    var httpStatus: HttpStatus = HttpStatus.OK,
    var type: String = "",
    var description: String = "",
    var body: String = ""
) {
    fun getIntStatus(): Int {
        return httpStatus.value()
    }
}