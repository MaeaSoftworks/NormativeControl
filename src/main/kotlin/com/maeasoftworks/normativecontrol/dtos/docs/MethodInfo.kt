package com.maeasoftworks.normativecontrol.dtos.docs

class MethodInfo(
    var root: String = "",
    var path: String = "",
    var type: String = "",
    var description: String = "",
    var queryParams: List<Parameter>? = null,
    var bodyParams: List<Parameter>? = null,
    var responses: List<Response>? = null
)