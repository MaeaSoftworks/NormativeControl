package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.FailureCause
import com.maeasoftworks.polonium.enums.Status

/**
 * Document representation
 * @param file file as `ByteArray`
 * @param status document status (`QUEUE` by default)
 * @param failureCause failure type (`NONE` by default)
 */
class DocumentData(
    var file: ByteArray = ByteArray(0),
    var status: Status = Status.QUEUE,
    var failureCause: FailureCause = FailureCause.NONE
)
