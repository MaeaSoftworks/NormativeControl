package normativecontrol.core.abstractions.schema

import kotlinx.serialization.Serializable
import normativecontrol.core.abstractions.chapters.Chapter

@Serializable
class Schema(
    val chapters: Array<Chapter>
)