package normativecontrol.core.predefined

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.Handler
import normativecontrol.core.mocktypes.Metadata
import normativecontrol.shared.warn
import org.slf4j.LoggerFactory

@Handler(Metadata::class, Predefined::class)
class MetadataHandler: AbstractHandler<Metadata>() {
    private val suppressor = """@suppress\((\d+)\)""".toRegex()

    context(VerificationContext)
    override fun handle(element: Metadata) {
        findSuppressions(element)
    }

    context(VerificationContext)
    private fun findSuppressions(element: Metadata) {
        val text = element.meta.keywords
        val suppress = suppressor.findAll(text).toList()
        if (suppress.isNotEmpty()) {
            suppress.forEach { match ->
                val code = match.groups.last()?.value?.toInt() ?: return@forEach
                configuration.state.suppressed.add(code)
            }
            logger.warn { "Enabled suppressions for mistakes with codes: ${configuration.state.suppressed.joinToString()}" }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MetadataHandler::class.java)
    }
}