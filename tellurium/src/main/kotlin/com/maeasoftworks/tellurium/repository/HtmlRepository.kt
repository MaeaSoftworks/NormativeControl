package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.DocumentRender
import org.springframework.data.repository.CrudRepository

interface HtmlRepository : CrudRepository<DocumentRender, String> {
    fun findByDocumentId(documentId: String): DocumentRender?
}