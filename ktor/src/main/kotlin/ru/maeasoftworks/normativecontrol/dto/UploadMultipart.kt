package ru.maeasoftworks.normativecontrol.dto

data class UploadMultipart(
    val file: ByteArray,
    val accessKey: String
)