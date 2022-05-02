package com.maeasoftworks.normativecontrol.daos


import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Table

@Table
@Entity
class DocumentFile(
    @Id
    val id: String,
    @Lob
    val file: ByteArray
)