package ru.maeasoftworks.normativecontrol.api.shared.extensions

import ru.maeasoftworks.normativecontrol.api.shared.modules.FileStorage

suspend fun FileStorage.uploadFile(documentId: String, file: ByteArray, accessKey: String) =
    putObject(file, documentId, mapOf("accessKey" to accessKey))

suspend fun FileStorage.uploadSourceDocument(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(source(documentId), file, accessKey)

suspend fun FileStorage.uploadDocumentRender(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(render(documentId), file, accessKey)

suspend fun FileStorage.uploadDocumentConclusion(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile(conclusion(documentId), file, accessKey)

fun source(documentId: String) = "$documentId/source.docx"

fun render(documentId: String) = "$documentId/conclusion.html"

fun conclusion(documentId: String) = "$documentId/conclusion.docx"