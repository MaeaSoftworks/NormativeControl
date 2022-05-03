package com.maeasoftworks.normativecontrol.daos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.maeasoftworks.normativecontrol.dtos.ErrorType
import org.hibernate.annotations.GenericGenerator
import javax.persistence.*

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
    constructor(errorType: ErrorType, documentId: String?) : this(documentId, -1, -1, -1, errorType)

    // unknown chapter
    constructor(paragraph: Int, run: Int, errorType: ErrorType, documentId: String?) : this(
        documentId,
        -1,
        paragraph,
        run,
        errorType
    )

    // to all chapter
    constructor(chapter: Long, errorType: ErrorType, documentId: String?) : this(
        documentId,
        chapter.toInt(),
        -1,
        -1,
        errorType
    )

    // to all paragraphId
    constructor(paragraph: Int, errorType: ErrorType, documentId: String?) : this(
        documentId,
        -1,
        paragraph,
        -1,
        errorType
    )


}