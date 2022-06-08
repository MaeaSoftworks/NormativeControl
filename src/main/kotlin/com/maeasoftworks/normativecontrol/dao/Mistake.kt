package com.maeasoftworks.normativecontrol.dao

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.normativecontrol.documentation.annotations.Documentation
import com.maeasoftworks.normativecontrol.documentation.annotations.PropertyDocumentation
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "document_mistakes")
@Documentation("docs.entity.Mistake.info")
@IdClass(MistakeKey::class)
class Mistake(
    @NotNull
    @get:JsonIgnore
    @get:JsonProperty(value = "document-id")
    @Column(name = "document_id")
    val documentId: String?,

    @Id
    @JsonIgnore
    @Suppress("unused")
    @Column(name = "mistake_id")
    var mistakeId: Long = 0,

    @PropertyDocumentation("docs.entity.Mistake.prop1")
    @get:JsonProperty(value = "paragraph-id")
    @Column(name = "paragraph_id")
    val paragraphId: Int?,

    @PropertyDocumentation("docs.entity.Mistake.prop2")
    @get:JsonProperty(value = "run-id")
    @Column(name = "run_id")
    val runId: Int?,

    @NotNull
    @PropertyDocumentation("docs.entity.Mistake.prop3", MistakeType::class)
    @get:JsonProperty(value = "mistake-type")
    @Enumerated(EnumType.STRING)
    @Column(name = "mistake_type")
    val mistakeType: MistakeType,

    @PropertyDocumentation("docs.entity.Mistake.prop4")
    val description: String? = null
)
