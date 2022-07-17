package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Table
@Entity
class DocumentRender (
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    val documentId: String,
    @Column(columnDefinition="TEXT")
    val html: String
)