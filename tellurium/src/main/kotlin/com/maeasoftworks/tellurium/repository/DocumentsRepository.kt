package com.maeasoftworks.tellurium.repository

import com.maeasoftworks.tellurium.dao.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DocumentsRepository : JpaRepository<Document, String> {
    @Query(
        "select d.document_id, d.access_key, d.file_password from documents d where d.document_id = ?1",
        nativeQuery = true
    )
    fun findCredentialsByDocumentId(documentId: String): DocumentCredentials?

    @Query("select d.document_id, (lo_get(d.docx)) as bytes from documents d where d.document_id = ?1", nativeQuery = true)
    fun findDocxByDocumentId(documentId: String): DocumentDocx?

    @Query("select (select d.docx from documents d where d.document_id = ?1) is not null", nativeQuery = true)
    fun existDocxByDocumentId(documentId: String): Boolean

    @Query("select d.document_id, d.html from documents d where d.document_id = ?1", nativeQuery = true)
    fun findHtmlByDocumentId(documentId: String): DocumentHtml?

    @Query(
        """ 
        select 
            mistake[1] as mistakeId,
            mistake[2] as paragraphId,
            mistake[3] as runId,
            mistake[4] as mistakeType,
            mistake[5] as mistakeDescription
        from (
            select regexp_split_to_array(agg.mis, ';') 
            from (
                select replace(replace(replace(translate(cast(dm as text), '()', ''), '""', '%'), '"', ''), '%', '"')
                as mis
                from (
                    select unnest(string_to_array(d.mistakes, '~'))
                    as mistake
                    from documents d
                    where d.document_id = ?1)
                as dm
                offset 1
            ) 
            as agg
        )
        as dt(mistake)
        """,
        nativeQuery = true
    )
    fun findMistakesByDocumentId(documentId: String): List<IMistake>
}