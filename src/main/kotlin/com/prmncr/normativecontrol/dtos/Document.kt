package com.prmncr.normativecontrol.dtos

data class Document (
    val id: String,
    val file: ByteArray,
    var state: State = State.QUEUE,
    var result: Result?
)