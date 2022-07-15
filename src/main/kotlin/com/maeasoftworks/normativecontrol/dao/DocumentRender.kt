package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table
@Entity
class DocumentRender(
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    val documentId: String,
    @Column(columnDefinition = "TEXT")
    val html: String
)