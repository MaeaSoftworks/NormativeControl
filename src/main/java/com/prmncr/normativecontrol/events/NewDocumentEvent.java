package com.prmncr.normativecontrol.events;

import org.springframework.context.ApplicationEvent;

public class NewDocumentEvent extends ApplicationEvent {
	private final String documentId;

	public NewDocumentEvent(Object source, String documentId) {
		super(source);
		this.documentId = documentId;
	}

	public String getDocumentId() {
		return documentId;
	}
}
