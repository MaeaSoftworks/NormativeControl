package ru.maeasoftworks.normativecontrol.students.dto

data class UploadMultipart(
    val file: ByteArray,
    val accessKey: String
)