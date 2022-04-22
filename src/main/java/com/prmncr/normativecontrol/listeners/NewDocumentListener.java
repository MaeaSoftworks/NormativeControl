package com.prmncr.normativecontrol.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prmncr.normativecontrol.dbos.DocumentData;
import com.prmncr.normativecontrol.dbos.DocumentFile;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.events.SaveDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentDataRepository;
import com.prmncr.normativecontrol.repositories.DocumentFileRepository;
import com.prmncr.normativecontrol.services.DocumentHandler;
import com.prmncr.normativecontrol.services.DocumentQueue;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NewDocumentListener {
    private final DocumentQueue storage;
    private final DocumentHandler handler;
    private final ApplicationEventPublisher publisher;
    private final DocumentDataRepository dataRepository;
    private final DocumentFileRepository fileRepository;

    @Async
    @EventListener
    public void handleDocument(NewDocumentEvent event) {
        val document = storage.getById(event.getDocumentId());
        assert document != null;
        document.setState(State.PROCESSING);
        handler.handle(document);
        publisher.publishEvent(new SaveDocumentEvent(this, document.getId()));
    }

    @Async
    @EventListener
    public void saveDocument(SaveDocumentEvent event) throws JsonProcessingException {
        var document = storage.getById(event.getDocumentId());
        assert document != null;
        dataRepository.save(new DocumentData(document.getId(), document.getResult().getErrors()));
        fileRepository.save(new DocumentFile(document.getId(), document.getFile()));
        storage.remove(document.getId());
    }
}
