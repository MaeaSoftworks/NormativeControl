package normativecontrol.core

import java.io.ByteArrayOutputStream

data class Result(
    val docx: ByteArrayOutputStream,
    val html: String,
    val statistics: Statistics
)