package com.prmncr.normativecontrol.dtos;

public class Result {
	public boolean isError;
	public String result;

	public Result(boolean isError, String result) {
		this.isError = isError;
	    this.result = result;
	}

	public Result(String result) {
		this.isError = false;
		this.result = result;
	}
}
