package normativecontrol.core.configurations

import normativecontrol.core.utils.lateinitVal

object VerificationConfiguration {
    var forceStyleInlining: Boolean by lateinitVal()

    inline fun initialize(fn: VerificationConfiguration.() -> Unit) {
        fn.invoke(VerificationConfiguration)
    }
}