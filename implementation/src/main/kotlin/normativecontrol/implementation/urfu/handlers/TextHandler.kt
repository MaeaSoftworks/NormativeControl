package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.rendering.html.span
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.TextUtils
import org.docx4j.wml.Text

@Handler(Text::class, UrFUConfiguration::class)
internal class TextHandler : AbstractHandler<Text>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: Text) {
        val rawText = TextUtils.getText(element)
        render append span {
            content = rawText.replace("<", "&lt;").replace(">", "&gt;")
        }
    }
}