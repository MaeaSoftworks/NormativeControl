package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.Status

class DocumentData(
    var file: ByteArray = ByteArray(0),
    var status: Status = Status.QUEUE,
    var failureType: FailureType = FailureType.NONE
)
