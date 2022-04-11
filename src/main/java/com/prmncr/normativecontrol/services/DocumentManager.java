package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DocumentManager {
    private final DocumentQueue storage;
    private final DocumentRepository repository;
    private final ApplicationEventPublisher publisher;

    public String addToQueue(byte[] file) {
        val document = new Document(UUID.randomUUID().toString(), file);
        storage.put(document);
        publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
        return document.getId();
    }

    public State getStatus(String id) {
        return storage.getById(id).getState();
    }

    public Result getResult(String id) {
        val result = storage.getById(id).getResult();
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
