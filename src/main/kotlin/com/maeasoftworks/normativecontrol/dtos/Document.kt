package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.dtos.enums.FailureType
import com.maeasoftworks.normativecontrol.dtos.enums.State

class Document(
    val id: String,
    val accessKey: String,
    var file: ByteArray = ByteArray(0),
    var state: State = State.QUEUE,
    var failureType: FailureType = FailureType.NONE
)