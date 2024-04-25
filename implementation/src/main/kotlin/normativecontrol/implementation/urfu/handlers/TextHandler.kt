package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.rendering.html.span
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.TextUtils
import org.docx4j.wml.Text

internal class TextHandler : Handler<Text>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: Text) {
        val rawText = TextUtils.getText(element)
        render append span {
            content = rawText.replace("<", "&lt;").replace(">", "&gt;")
        }
    }

    @HandlerFactory(Text::class, UrFUConfiguration::class)
    companion object : Factory<TextHandler> {
        override fun create() = TextHandler()
    }
}