package com.prmncr.normativecontrol.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prmncr.normativecontrol.dbos.ProcessedDocument;
import com.prmncr.normativecontrol.dtos.Document;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.dtos.Result;
import com.prmncr.normativecontrol.dtos.State;
import com.prmncr.normativecontrol.events.NewDocumentEvent;
import com.prmncr.normativecontrol.repositories.DocumentRepository;
import com.prmncr.normativecontrol.serializers.ByteArraySerializer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.Lob;
import java.util.List;
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

    public Object getFile(String id) throws JsonProcessingException {
        val fileObject = repository.findById(id);
        if (!fileObject.isPresent()) {
            return null;
        }
        return new Object() {
            @JsonSerialize(using = ByteArraySerializer.class)
            public byte[] file = fileObject.get().getFile();
            public final List<Error> errors = fileObject.get().getDeserializedErrors();
        };
    }

    public void saveResult(String id) throws JsonProcessingException {
        val document = queue.getById(id);
        if (document == null) {
            throw new NullPointerException();
        }
        val doc = new ProcessedDocument(document.getId(), document.getFile(), document.getResult().getErrors());
        repository.save(doc);
        queue.remove(id);
    }

    public void dropDatabase() {
        repository.deleteAll();
    }

    public void delete(String id) {
        queue.remove(id);
    }
}
