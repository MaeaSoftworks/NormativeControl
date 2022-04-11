package com.prmncr.normativecontrol.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentQueue;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NewDocumentListener {
    private final DocumentRepository repository;
    private final DocumentQueue storage;
    private final DocumentHandler handler;

    @Async
    @EventListener
    @Transactional
    public void handleDocument(NewDocumentEvent event) {
        val document = storage.getById(event.getDocumentId());
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
