package com.prmncr.normativecontrol.dtos

class Result {
    private val isFail: Boolean
    private val failureType: FailureType
    val errors: List<Error>

    constructor(failureType: FailureType) {
        isFail = true
        this.failureType = failureType
        errors = listOf()
    }

    constructor(errors: List<Error>) {
        isFail = false
        failureType = FailureType.NONE
        this.errors = errors
    }
}