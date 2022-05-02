package com.maeasoftworks.normativecontrol.dtos

class Result {
    val isFail: Boolean
    val failureType: FailureType
    val errors: List<Error>?

    constructor(failureType: FailureType) {
        isFail = true
        this.failureType = failureType
        errors = null
    }

    constructor(errors: List<Error>?) {
        isFail = false
        failureType = FailureType.NONE
        this.errors = errors
    }
}
