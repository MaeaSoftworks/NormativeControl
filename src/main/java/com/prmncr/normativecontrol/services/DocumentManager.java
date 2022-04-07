package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@SuppressWarnings("ClassCanBeRecord")
public class DocumentManager {
    private final DocumentStorage storage;
    private final DocumentRepository repository;
    private final ApplicationEventPublisher publisher;

    public DocumentManager(DocumentStorage storage,
                           DocumentRepository repository,
                           ApplicationEventPublisher publisher) {
        this.storage = storage;
        this.repository = repository;
        this.publisher = publisher;
    }

    public String addToQueue(byte[] file) {
        var document = new Document(UUID.randomUUID().toString(), file);
        storage.put(document);
        publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
        return document.getId();
    }

    public State getStatus(String id) {
        return storage.getById(id).getState();
    }

    public Result getResult(String id) {
        var result = storage.getById(id).getResult();
        storage.remove(id);
        return result;
    }

    public ProcessedDocument getFile(String id) {
        return repository.findById(id).orElse(null);
    }

    public void dropDatabase() {
        repository.deleteAll();
    }
}
