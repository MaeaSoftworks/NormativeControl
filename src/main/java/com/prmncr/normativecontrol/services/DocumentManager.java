package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentManager {
    private final DocumentStorage documentStorage;
    private final DocumentRepository documentRepository;
    private final ApplicationEventPublisher publisher;

    public DocumentManager(
            DocumentStorage documentStorage,
            DocumentRepository documentRepository,
            ApplicationEventPublisher publisher) {
        this.documentStorage = documentStorage;
        this.documentRepository = documentRepository;
        this.publisher = publisher;
    }

    public String addToQueue(byte[] file) {
        var document = new Document(UUID.randomUUID().toString(), file);
        documentStorage.put(document);
        publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
        return document.getId();
    }

    public State getStatus(String id) {
        return documentStorage.getById(id).state;
    }

    public Result getResult(String id) {
        var result = documentStorage.getById(id).result;
        documentStorage.remove(id);
        return result;
    }

    public String loadResult(String id) {
        var result = documentRepository.findById(id);
        if (result.isPresent()) {
            return "html will be here";
        } else {
            return "file not found";
        }
    }
}
