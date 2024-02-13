package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.domain.dao.Document
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InstantSerializer
import java.time.Instant

@Serializable
data class DocumentListResponse(
    val id: String,
    @Serializable(InstantSerializer::class)
    val timestamp: Instant
) {
    constructor(document: Document) : this(document.id, document.timestamp)
}