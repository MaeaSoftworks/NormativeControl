package com.maeasoftworks.normativecontrol.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import com.maeasoftworks.normativecontrol.parser.enums.MistakeType
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
@Documentation("Represents mistake found in document.")
class Mistake(
    @JsonIgnore
    val documentId: String?,
    @Suppress("unused")
    @PropertyDocumentation("pointer to chapter in document")
    val chapterId: Int,
    @PropertyDocumentation("pointer to paragraph in document")
    val paragraphId: Int,
    @PropertyDocumentation("pointer to run in paragraph with <code>paragraphId</code>")
    val runId: Int,
    @PropertyDocumentation("mistake type. Can be:", MistakeType::class)
    val mistakeType: MistakeType,
    @PropertyDocumentation("describes expected and found values. Format: <code>\"\${\$FOUND}/\${\$EXPECTED}\"</code>.<br>Also can contains some debug information")
    val description: String = ""
) {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @JsonIgnore
    var id: String = ""

    // to page
    constructor(documentId: String?, mistakeType: MistakeType, description: String = "") : this(
        documentId,
        -1,
        -1,
        -1,
        mistakeType,
        description
    )

    // unknown chapter
    constructor(
        documentId: String?,
        paragraph: Int,
        run: Int,
        mistakeType: MistakeType,
        description: String = ""
    ) : this(
        documentId,
        -1,
        paragraph,
        run,
        mistakeType,
        description
    )

    // to all chapter
    constructor(documentId: String?, chapter: Long, mistakeType: MistakeType, description: String = "") : this(
        documentId,
        chapter.toInt(),
        -1,
        -1,
        mistakeType,
        description
    )

    // to all paragraphId
    constructor(documentId: String?, paragraph: Int, mistakeType: MistakeType, description: String = "") : this(
        documentId,
        -1,
        paragraph,
        -1,
        mistakeType,
        description
    )
}