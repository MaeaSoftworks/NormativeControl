package normativecontrol.core.configurations

import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.shared.lateinitVal

class VerificationConfiguration {
    var chapterConfiguration: ChapterConfiguration by lateinitVal()
    var forceStyleInlining: Boolean by lateinitVal(false)

    inline fun initialize(fn: VerificationConfiguration.() -> Unit): VerificationConfiguration {
        return VerificationConfiguration().apply(fn)
    }
}