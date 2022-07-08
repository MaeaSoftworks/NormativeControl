package com.maeasoftworks.normativecontrol.repository

import com.maeasoftworks.normativecontrol.dao.DocumentRender
import org.springframework.data.repository.CrudRepository

interface HtmlRepository: CrudRepository<DocumentRender, String> {
    fun findByDocumentId(documentId: String): DocumentRender?
}