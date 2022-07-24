package com.maeasoftworks.tellurium.documentation

class Response(
    var httpStatus: String,
    var type: String? = null,
    var description: String = "",
    var body: String = ""
)