package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DocumentManager {
	private final DocumentStorage documentStorage;
	private final ApplicationEventPublisher publisher;

	public DocumentManager(DocumentStorage documentStorage, ApplicationEventPublisher publisher) {
		this.documentStorage = documentStorage;
	    this.publisher = publisher;
	}

	public String addToQueue(Path path) {
		var document = new Document(path, UUID.randomUUID().toString());
		documentStorage.put(document);
		publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
		return document.getId();
	}

	public State getStatus(String id) {
		return documentStorage.getById(id).getState();
	}

	public String getResult(String id) {
		var result = documentStorage.getById(id).getResult().result;
		try {
			documentStorage.remove(id);
		} catch (IOException e) {
			return e.getMessage();
		}
		return result;
	}
}
