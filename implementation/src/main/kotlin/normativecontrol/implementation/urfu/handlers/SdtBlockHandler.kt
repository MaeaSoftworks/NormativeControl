package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.SdtBlock

@Handler(SdtBlock::class, UrFUConfiguration::class)
class SdtBlockHandler : AbstractHandler<SdtBlock>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: SdtBlock) {
        render.inLastElementScope {
            state.inSdtBlock = true
            element.sdtContent.content.iterate(1) { child, _ -> // inspect only contents title
                runtime.handlers[child]?.handleElement(child)
            }
            state.inSdtBlock = false
            state.sinceSdtBlock = 0
        }
        state.suppressChapterRecognition = false
    }
}