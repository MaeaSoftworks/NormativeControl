package com.maeasoftworks.tellurium.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Table
@Entity
class DocumentCredentials(
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    @NotBlank
    val documentId: String = "",
    @Column(name = "access_key")
    @NotBlank
    val accessKey: String = "",
    @NotBlank
    val password: String = ""
)
