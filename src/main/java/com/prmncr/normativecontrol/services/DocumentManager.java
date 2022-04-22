package com.prmncr.normativecontrol.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prmncr.normativecontrol.dbos.DocumentData;
import com.prmncr.normativecontrol.dbos.DocumentFile;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentDataRepository;
import com.prmncr.normativecontrol.repositories.DocumentFileRepository;
import com.prmncr.normativecontrol.serializers.ByteArraySerializer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public State getState(String id) {
        val document = queue.getById(id);
        if (document != null) {
            return document.getState();
        } else {
            return dataRepository.existsById(id) ? State.READY : null;
        }
    }

    public DocumentData getData(String id) {
        return dataRepository.findById(id).orElse(null);
    }

    public DocumentFile getFile(String id) {
        return fileRepository.findById(id).orElse(null);
    }

    public void dropDatabase() {
        dataRepository.deleteAll();
    }

    public void delete(String id) {
        queue.remove(id);
    }
}
