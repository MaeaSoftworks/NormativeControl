package com.prmncr.normativecontrol.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentStorage;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewDocumentListener {
    private final DocumentRepository repository;
    private final DocumentStorage storage;
    private final DocumentHandler handler;

    public NewDocumentListener(DocumentRepository repository, DocumentStorage storage, DocumentHandler handler) {
        this.repository = repository;
        this.storage = storage;
        this.handler = handler;
    }

    @Async
    @EventListener
    @Transactional
    public void handleDocument(NewDocumentEvent event) {
        var document = storage.getById(event.getDocumentId());
        document.setState(State.PROCESSING);
        handler.handle(document);
        document.setState(State.READY);
        ProcessedDocument doc;
        try {
            doc = new ProcessedDocument(document.getId(), document.getFile(), document.getResult().getErrors());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        repository.save(doc);
    }
}
