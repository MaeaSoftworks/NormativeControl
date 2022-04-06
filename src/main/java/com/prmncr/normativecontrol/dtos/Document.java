package com.prmncr.normativecontrol.dtos;

public class Document {
    private final String id;
    private final byte[] file;
    public State state;
    public Result result;

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
}
