package com.maeasoftworks.normativecontrol.parser.model

import com.maeasoftworks.normativecontrol.parser.enums.FailureType
import com.maeasoftworks.normativecontrol.parser.enums.Status

class Document(
    val id: String,
    val accessKey: String,
    var file: ByteArray = ByteArray(0),
    var status: Status = Status.QUEUE,
    var failureType: FailureType = FailureType.NONE
)