package com.maeasoftworks.normativecontrol.dtos

data class Document(
    val id: String,
    var file: ByteArray,
    var state: State = State.QUEUE,
    var result: Result? = null
)