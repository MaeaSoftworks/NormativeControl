package normativecontrol.core.data

import java.io.ByteArrayOutputStream

data class Result(
    val docx: ByteArrayOutputStream,
    val html: String,
    val statistics: Statistics
)