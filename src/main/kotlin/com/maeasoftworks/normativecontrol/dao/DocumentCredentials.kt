package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table
@Entity
class DocumentCredentials(
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    val documentId: String,
    @Column(name = "access_key")
    val accessKey: String,
    val password: String
)
