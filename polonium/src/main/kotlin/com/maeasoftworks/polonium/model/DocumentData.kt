package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.FailureType
import com.maeasoftworks.polonium.enums.Status

/**
 * Document representation
 * @param file file in `ByteArray` representation
 * @param status document status (`QUEUE` by default)
 * @param failureType failure type (`NONE` by default)
 * @author prmncr
 */
class DocumentData(
    var file: ByteArray = ByteArray(0),
    var status: Status = Status.QUEUE,
    var failureType: FailureType = FailureType.NONE
)
