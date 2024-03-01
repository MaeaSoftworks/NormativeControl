package normativecontrol.core.abstractions.chapters

import kotlinx.serialization.Serializable

@Serializable
open class Chapter @Deprecated("Use schema initialization instead of direct calls") constructor(
    val code: String,
    val validNames: Array<String>,
    val validNextChapterCodes: Array<String>
)