package ru.maeasoftworks.normativecontrol.api.students.dto

data class UploadMultipart(
    val file: ByteArray,
    val accessKey: String
)