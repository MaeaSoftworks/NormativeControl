package com.prmncr.normativecontrol.dbos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Getter
@AllArgsConstructor
@Table
@Entity
public class DocumentFile {
    @Id
    private String id;
    @Lob
    private byte[] file;

    protected DocumentFile() {

    }
}