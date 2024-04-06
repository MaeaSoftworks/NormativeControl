package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.configurations.VerificationConfiguration

object PredefinedConfiguration : Configuration(Chapter.Undefined, VerificationConfiguration().initialize {
    chapterConfiguration = ChapterConfiguration.empty
}) {
    override fun mapHandler(element: Any?): Handler<*, *, *>? = when(element) {
        is JAXBElement<*> -> JAXBElementHandler
        else -> null
    }
}