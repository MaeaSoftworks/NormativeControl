package com.prmncr.normativecontrol.dtos;

public class Result {
    private final boolean isError;
    private final String result;
    private ResultBody body;

    public Result(boolean isError, String result) {
        this.isError = isError;
        this.result = result;
    }

    public Result(String result) {
        this.isError = false;
        this.result = result;
    }

    public Result(ResultBody body) {
        this.isError = false;
        this.result = null;
        this.body = body;
    }

    public boolean isError() {
        return isError;
    }

    public String getResult() {
        return result;
    }

    public ResultBody getBody() {
        return body;
    }

    public void setBody(ResultBody body) {
        this.body = body;
    }
}
