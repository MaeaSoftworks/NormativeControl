package com.maeasoftworks.normativecontrol.daos

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table
@Entity
class DocumentKey (
    @Id
    @JsonIgnore
    val documentId: String,
    val accessKey: String?
)