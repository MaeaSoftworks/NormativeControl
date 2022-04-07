package com.prmncr.normativecontrol.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentRepository;
import com.prmncr.normativecontrol.services.DocumentStorage;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NewDocumentListener {
    private final DocumentRepository documentRepository;
    private final DocumentStorage documentStorage;
    private final DocumentHandler documentHandler;

    public NewDocumentListener(DocumentRepository documentRepository,
                               DocumentStorage documentStorage,
                               DocumentHandler documentHandler) {
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
        this.documentHandler = documentHandler;
    }

    @Async
    @EventListener
    public void handleDocument(NewDocumentEvent event) {
        var document = documentStorage.getById(event.getDocumentId());
        document.setState(State.PROCESSING);
        documentHandler.handle(document);
        document.setState(State.READY);
        ProcessedDocument doc;
        try {
            doc = new ProcessedDocument(document.getId(), document.getFile(), document.getResult().getErrors());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        documentRepository.save(doc);
    }
}
