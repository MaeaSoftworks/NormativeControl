package normativecontrol.core.data

import java.io.ByteArrayOutputStream

/**
 * Verification results.
 * @param docx Document with mistakes in comments
 * @param html Rendered version of document
 * @param statistics Statistics of verification
 */
data class Result(
    val docx: ByteArrayOutputStream,
    val html: String,
    val statistics: Statistics
)