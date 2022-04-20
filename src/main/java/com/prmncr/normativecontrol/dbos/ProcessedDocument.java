package com.prmncr.normativecontrol.dbos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prmncr.normativecontrol.dtos.Error;
import com.prmncr.normativecontrol.serializers.ByteArraySerializer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table
public class ProcessedDocument {
    @Id
    @JsonIgnore
    private String id;
    @JsonSerialize(using = ByteArraySerializer.class)
    @Lob
    private byte[] file;
    private String errors;

    public ProcessedDocument(String id, byte[] file, List<Error> errors) throws JsonProcessingException {
        this.id = id;
        this.file = file;
        this.errors = new ObjectMapper().writeValueAsString(errors);
    }

    public ProcessedDocument(String id, byte[] file, String errors) {
        this.id = id;
        this.file = file;
        this.errors = errors;
    }

    public ProcessedDocument() {

    }

    public String getId() {
        return id;
    }

    public byte[] getFile() {
        return file;
    }

    public String getErrors() {
        return errors;
    }

    public List<Error> getDeserializedErrors() throws JsonProcessingException {
        return new ObjectMapper().readValue(errors, new TypeReference<>() {
        });
    }
}
