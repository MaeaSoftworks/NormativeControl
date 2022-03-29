package com.prmncr.normativecontrol.dtos;

import java.nio.file.Path;

public class Document {
	private final Path path;
	private final String id;
	private State state;
	private Result result;

	public Document(Path path, String id) {
		this.path = path;
		this.id = id;
		this.state = State.QUEUE;
	}

	public Document(Path path, String id, State state) {
	    this.path = path;
		this.id = id;
		this.state = state;
	}

	public Path getPath() {
		return path;
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
