package com.prmncr.normativecontrol.dtos;

import lombok.Getter;

import java.util.List;

public class Result {
    @Getter
    private final boolean isFail;
    @Getter
    private final FailureType failureType;
    @Getter
    private final List<Error> errors;

    public Result(FailureType failureType) {
        this.isFail = true;
        this.failureType = failureType;
        errors = null;
    }

    public Result(List<Error> errors) {
        this.isFail = false;
        this.failureType = FailureType.NONE;
        this.errors = errors;
    }
}
