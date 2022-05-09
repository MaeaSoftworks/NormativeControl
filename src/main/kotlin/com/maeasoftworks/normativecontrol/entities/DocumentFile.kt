package com.maeasoftworks.normativecontrol.entities


import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table

@Table
@Entity
class DocumentFile(
    @Id
    @JsonIgnore
    val documentId: String,
    val accessKey: String,
    @Lob
    val bytes: ByteArray
)