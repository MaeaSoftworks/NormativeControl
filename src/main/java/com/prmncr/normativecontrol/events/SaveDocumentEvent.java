package com.prmncr.normativecontrol.events;

import org.springframework.context.ApplicationEvent;

public class SaveDocumentEvent extends ApplicationEvent {
    private final String documentId;

    public SaveDocumentEvent(Object source, String documentId) {
        super(source);
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}
