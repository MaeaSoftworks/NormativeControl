package normativecontrol.core.implementations.ufru.utils

import normativecontrol.core.contexts.VerificationContext

context(VerificationContext)
fun describeState(): PointerState {
    if (isHeader) {
        if (sinceHeader == 0) return PointerState.Header
    }
    if (sinceHeader == 1) return PointerState.UnderHeader
    return PointerState.Text
}