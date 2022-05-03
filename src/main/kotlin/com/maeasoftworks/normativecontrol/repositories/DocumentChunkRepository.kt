package com.maeasoftworks.normativecontrol.repositories

import com.maeasoftworks.normativecontrol.daos.DocumentChunk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface DocumentChunkRepository : CrudRepository<DocumentChunk, String> {
    fun findAllByDocumentIdOrderByChunkId(documentId: String) : List<DocumentChunk>

    fun existsDocumentChunkByDocumentId(documentId: String) : Boolean
}