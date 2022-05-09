package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.enums.FailureType
import com.maeasoftworks.normativecontrol.parser.enums.State

class Document(
    val id: String,
    val accessKey: String,
    var file: ByteArray = ByteArray(0),
    var state: State = State.QUEUE,
    var failureType: FailureType = FailureType.NONE
)