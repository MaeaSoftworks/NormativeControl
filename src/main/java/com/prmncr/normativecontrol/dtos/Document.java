package com.prmncr.normativecontrol.dtos;

public class Document {
    private State state;
    private Result result;
    private final String id;
    private final byte[] file;

    public Document(String id, byte[] file) {
        this.id = id;
        this.state = State.QUEUE;
        this.file = file;
    }

    public byte[] getFile() {
        return file;
    }

    public String getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
