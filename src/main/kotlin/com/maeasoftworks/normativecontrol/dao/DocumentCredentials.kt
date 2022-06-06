package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table
@Entity
class DocumentCredentials(
    @Id
    @JsonIgnore
    val documentId: String,
    val accessKey: String?
)