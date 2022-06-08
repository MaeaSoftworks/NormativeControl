package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Table
@Entity
class DocumentBytes(
    @Id
    @JsonIgnore
    @Column(name = "document_id")
    val documentId: String,
    @Lob
    val bytes: ByteArray
)
