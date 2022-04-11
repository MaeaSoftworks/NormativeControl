package com.prmncr.normativecontrol.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DocumentManager {
    private final DocumentQueue queue;
    private final DocumentRepository repository;
    private final ApplicationEventPublisher publisher;

    public String addToQueue(byte[] file) {
        val document = new Document(UUID.randomUUID().toString(), file);
        queue.put(document);
        publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
        return document.getId();
    }

    public State getState(String id) {
        val document = queue.getById(id);
        if (document != null) {
            return document.getState();
        }
        return null;
    }

    public Result getResult(String id) {
        val document = queue.getById(id);
        if (document == null) {
            return null;
        }
        return document.getResult();
    }

    public ProcessedDocument getFile(String id) {
        return repository.findById(id).orElse(null);
    }

    public void saveResult(String id) throws JsonProcessingException {
        val document = queue.getById(id);
        if (document == null) {
            throw new NullPointerException();
        }
        val doc = new ProcessedDocument(document.getId(), document.getFile(), document.getResult().getErrors());
        queue.remove(id);
        repository.save(doc);
    }

    public void dropDatabase() {
        repository.deleteAll();
    }
}
