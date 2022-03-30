package com.prmncr.normativecontrol.dtos;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class ProcessedDocument {
	@Id
	private String id;
	@Lob
	private byte[] file;

	public ProcessedDocument(String id, byte[] file) {
		this.id = id;
		this.file = file;
	}

	public ProcessedDocument() {

	}
}
