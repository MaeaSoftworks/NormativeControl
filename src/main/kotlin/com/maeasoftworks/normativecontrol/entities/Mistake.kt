package com.maeasoftworks.normativecontrol.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
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
@Documentation("docs.entity.Mistake.info")
class Mistake(
    @JsonIgnore
    @get:JsonProperty(value = "document-id")
    val documentId: String?,
    @Suppress("unused")
    @PropertyDocumentation("docs.entity.Mistake.prop0")
    @get:JsonProperty(value = "chapter-id")
    val chapterId: Int,
    @PropertyDocumentation("docs.entity.Mistake.prop1")
    @get:JsonProperty(value = "paragraph-id")
    val paragraphId: Int,
    @PropertyDocumentation("docs.entity.Mistake.prop2")
    @get:JsonProperty(value = "run-id")
    val runId: Int,
    @PropertyDocumentation("docs.entity.Mistake.prop3", MistakeType::class)
    @get:JsonProperty(value = "mistake-type")
    val mistakeType: MistakeType,
    @PropertyDocumentation("docs.entity.Mistake.prop4")
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