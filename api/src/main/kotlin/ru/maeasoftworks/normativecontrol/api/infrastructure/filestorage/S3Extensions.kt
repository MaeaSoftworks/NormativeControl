package ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage

suspend fun FileStorage.uploadFile(documentId: String, file: ByteArray, fingerprint: String?) =
    if (fingerprint != null) putObject(file, documentId, "fingerprint" to fingerprint)
    else putObject(file, documentId)

suspend fun FileStorage.uploadSourceDocument(documentId: String, file: ByteArray, fingerprint: String?) =
    uploadFile(source(documentId), file, fingerprint)

suspend fun FileStorage.uploadDocumentRender(documentId: String, file: ByteArray, fingerprint: String?) =
    uploadFile(render(documentId), file, fingerprint)

suspend fun FileStorage.uploadDocumentConclusion(documentId: String, file: ByteArray, fingerprint: String?) =
    uploadFile(conclusion(documentId), file, fingerprint)

fun source(documentId: String) = "$documentId/source.docx"

fun render(documentId: String) = "$documentId/conclusion.html"

fun conclusion(documentId: String) = "$documentId/conclusion.docx"