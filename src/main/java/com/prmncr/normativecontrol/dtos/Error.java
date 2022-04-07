package com.prmncr.normativecontrol.dtos;

public class Error {
    private final long paragraph;
    private final long run;
    private final ErrorType errorType;

    public Error(long paragraph, long run, ErrorType errorType) {
        this.paragraph = paragraph;
        this.run = run;
        this.errorType = errorType;
    }

    public long getParagraph() {
        return paragraph;
    }

    public long getRun() {
        return run;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
