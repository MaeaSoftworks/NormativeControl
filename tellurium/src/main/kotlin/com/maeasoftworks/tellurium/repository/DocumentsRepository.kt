package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DocumentsRepository: JpaRepository<Document, String> {
    @Query("select d.document_id, d.access_key, d.file_password from documents d where d.document_id = ?1", nativeQuery = true)
    fun findCredentialsByDocumentId(documentId: String): DocumentCredentials?

    @Query("select d.document_id, d.docx from documents d where d.document_id = ?1", nativeQuery = true)
    fun findDocxByDocumentId(documentId: String): DocumentDocx?

    @Query("select (select d.docx from documents d where d.document_id = ?1) is null", nativeQuery = true)
    fun existDocxByDocumentId(documentId: String): Boolean

    @Query("select d.document_id, d.docx from documents d where d.document_id = ?1", nativeQuery = true)
    fun findHtmlByDocumentId(documentId: String): DocumentHtml?

    @Query("select d.mistakes from documents d where d.document_id = ?1", nativeQuery = true)
    fun findMistakesByDocumentId(documentId: String): List<Mistake>
}