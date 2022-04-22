package com.prmncr.normativecontrol.dbos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prmncr.normativecontrol.dtos.Error;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Getter
@Table
@Entity
public class DocumentData {
    @Id
    private String id;
    private String errors;

    public DocumentData(String id, List<Error> errors) throws JsonProcessingException {
        this.id = id;
        this.errors = new ObjectMapper().writeValueAsString(errors);
    }

    public DocumentData() {}

    public List<Error> getDeserializedErrors() throws JsonProcessingException {
        return new ObjectMapper().readValue(errors, new TypeReference<>() {});
    }
}
