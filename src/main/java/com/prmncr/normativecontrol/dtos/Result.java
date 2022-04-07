package com.prmncr.normativecontrol.dtos;

import java.util.List;

public class Result {
    private final boolean isFail;
    private final FailureType failureType;
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

    public boolean isFail() {
        return isFail;
    }

    public FailureType getFailureType() {
        return failureType;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
