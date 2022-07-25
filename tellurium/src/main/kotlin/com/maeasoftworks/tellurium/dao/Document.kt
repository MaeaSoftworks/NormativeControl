package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "documents")
class Document(
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    @NotBlank
    var documentId: String,

    @Column(name = "access_key")
    @NotBlank
    var accessKey: String,

    @Column(name = "file_password")
    @NotBlank
    var filePassword: String,

    @Lob
    var docx: ByteArray? = null,

    @Column(columnDefinition = "text")
    var html: String? = null,

    @Column(columnDefinition = "text")
    var mistakes: String
)