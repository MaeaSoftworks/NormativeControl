package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        return documentStorage.getById(id).getState();
    }

    public Result getResult(String id) {
        var result = documentStorage.getById(id).getResult();
        documentStorage.remove(id);
        return result;
    }

    public byte[] getFile(String id) {
        return Objects.requireNonNull(documentRepository.findById(id).orElse(null)).getFile();
    }

    public void dropDatabase() {
        documentRepository.deleteAll();
    }

    public String getSavedResult(String id) {
        return Objects.requireNonNull(documentRepository.findById(id).orElse(null)).getErrors();
    }
}
