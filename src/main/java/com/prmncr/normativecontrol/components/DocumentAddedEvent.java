package com.prmncr.normativecontrol.components;

import org.springframework.context.ApplicationEvent;

public class DocumentAddedEvent extends ApplicationEvent {
	public DocumentAddedEvent(Object source, Object document) {
		super(source);
	}
}
