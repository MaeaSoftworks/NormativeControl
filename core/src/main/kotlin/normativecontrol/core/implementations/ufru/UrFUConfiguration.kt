package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.handlers.*
import org.docx4j.wml.Br
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.docx4j.wml.R.Tab
import org.docx4j.wml.Text

object UrFUConfiguration : Configuration(
    Chapters.FrontPage,
    VerificationConfiguration().initialize {
        chapterConfiguration = ChapterConfiguration.create<Chapters>()
    },
    { GlobalState() }
) {
    override val VerificationContext.globalState: GlobalState
        get() = globalStateHolder as GlobalState

    override fun mapHandler(element: Any?): Handler<*, *, *>? = when(element) {
        is P -> PHandler
        is R -> RHandler
        is Text -> TextHandler
        is Br -> BrHandler
        is R.LastRenderedPageBreak -> RLastRenderedPageBreakHandler
        is Tab -> TabHandler
        else -> null
    }
}