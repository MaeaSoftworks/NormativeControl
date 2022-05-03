package com.maeasoftworks.normativecontrol.daos


import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

@Table
@Entity
class DocumentChunk(
    @JsonIgnore
    val documentId: String,
    var chunkId: Long = 0,
    @Lob
    val file: ByteArray
) {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    var id: String = ""
}