package com.maeasoftworks.normativecontrol.daos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
class DocumentError(
    @JsonIgnore
    val documentId: String?,
    val chapterId: Int,
    val paragraphId: Int,
    val runId: Int,
    val errorType: ErrorType?
) {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    var id: String = ""

    // to page
    constructor(documentId: String?, errorType: ErrorType) : this(documentId, -1, -1, -1, errorType)

    // unknown chapter
    constructor(documentId: String?, paragraph: Int, run: Int, errorType: ErrorType) : this(
        documentId,
        -1,
        paragraph,
        run,
        errorType
    )

    // to all chapter
    constructor(documentId: String?, chapter: Long, errorType: ErrorType) : this(
        documentId,
        chapter.toInt(),
        -1,
        -1,
        errorType
    )

    // to all paragraphId
    constructor(documentId: String?, paragraph: Int, errorType: ErrorType) : this(
        documentId,
        -1,
        paragraph,
        -1,
        errorType
    )


}