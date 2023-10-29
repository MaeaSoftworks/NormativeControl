package api.students.dto

import java.io.ByteArrayInputStream

data class File(
    val filename: String,
    val body: ByteArrayInputStream,
    val size: Long
)