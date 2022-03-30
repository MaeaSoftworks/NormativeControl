package com.prmncr.normativecontrol.dtos;

public class Document {
	private final String id;
	public State state;
	public Result result;
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
}
