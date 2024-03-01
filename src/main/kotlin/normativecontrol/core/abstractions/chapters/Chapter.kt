package normativecontrol.core.abstractions.chapters

import kotlinx.serialization.Serializable

@Serializable
open class Chapter(
    val code: String,
    val validNames: Array<String>,
    val validNextChapterCodes: Array<String>
)