package com.prmncr.normativecontrol.services;

import com.prmncr.normativecontrol.dbos.DocumentData;
import com.prmncr.normativecontrol.dbos.DocumentFile;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentDataRepository;
import com.prmncr.normativecontrol.repositories.DocumentFileRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@AllArgsConstructor
public class DocumentManager {
    private final DocumentQueue queue;
    private final DocumentDataRepository dataRepository;
    private final DocumentFileRepository fileRepository;
    private final ApplicationEventPublisher publisher;


    public String addToQueue(byte[] file) {
        val document = new Document(UUID.randomUUID().toString(), file);
        queue.put(document);
        publisher.publishEvent(new NewDocumentEvent(this, document.getId()));
        return document.getId();
    }

    @Transactional
    public State getState(String id) {
        val document = queue.getById(id);
        if (document != null) {
            return document.getState();
        } else {
            return dataRepository.existsById(id) ? State.READY : null;
        }
    }

    @Transactional
    public DocumentData getData(String id) {
        return dataRepository.findById(id).orElse(null);
    }

    @Transactional
    public DocumentFile getFile(String id) {
        return fileRepository.findById(id).orElse(null);
    }

    @Transactional
    public void dropDatabase() {
        dataRepository.deleteAll();
        
        fileRepository.deleteAll();
    }
}
