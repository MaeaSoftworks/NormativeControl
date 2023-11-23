package ru.maeasoftworks.normativecontrol.extensions

import ru.maeasoftworks.normativecontrol.modules.S3Client

suspend fun S3Client.uploadFile(documentId: String, file: ByteArray, accessKey: String) =
    putObject(file, documentId, mapOf("accessKey" to accessKey))

suspend fun S3Client.uploadSource(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile("${ documentId }/source.docx", file, accessKey)

suspend fun S3Client.uploadRender(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile("${ documentId }/conclusion.html", file, accessKey)

suspend fun S3Client.uploadConclusion(documentId: String, file: ByteArray, accessKey: String) =
    uploadFile("${ documentId }/conclusion.docx", file, accessKey)