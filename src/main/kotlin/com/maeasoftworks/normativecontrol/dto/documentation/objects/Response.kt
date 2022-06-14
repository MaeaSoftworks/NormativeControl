package com.maeasoftworks.normativecontrol.dto.documentation.objects

import org.springframework.http.HttpStatus

class Response(
    var httpStatus: HttpStatus = HttpStatus.OK,
    var type: String = "",
    var description: String = "",
    var body: String = ""
)
