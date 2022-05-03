package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.daos.DocumentError

class Result {
    val isFail: Boolean
    val failureType: FailureType
    val errors: List<DocumentError>

    constructor(failureType: FailureType) {
        isFail = true
        this.failureType = failureType
        errors = ArrayList()
    }

    constructor(errors: List<DocumentError>) {
        isFail = false
        failureType = FailureType.NONE
        this.errors = errors
    }
}
