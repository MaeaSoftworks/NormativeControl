package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.span
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.TextUtils
import org.docx4j.wml.Text

internal class TextHandler : Handler<Text>(), StateProvider<UrFUState> {
    private val inBrackets = """\[(.*?)]""".toRegex()
    private val removePages = """,\s*С\.(?:.*)*""".toRegex()
    private val removeAndMatchRanges = """(\d+)\s*-\s*(\d+)""".toRegex()
    private val matchReference = """(\d+)""".toRegex()

    context(VerificationContext)
    override fun handle(element: Text) {
        val rawText = TextUtils.getText(element)

        state.referencesInText.addAll(getAllReferences(rawText))
        render append span {
            content = rawText.replace("<", "&lt;").replace(">", "&gt;")
        }
    }

    private fun getAllReferences(text: String): Set<Int> {
        val set = mutableSetOf<Int>()
        val (refs, ranges) = findAllRanges(clearPages(findAllInBrackets(text))).let { it.first.toList() to it.second }
        ranges.forEach {
            for (i in it) {
                set += i
            }
        }
        findAllReferences(refs).forEach(set::add)
        return set
    }

    private fun findAllInBrackets(text: String): Sequence<String> {
        return inBrackets.findAll(text).map { it.groups[1]!!.value }
    }

    private fun clearPages(refs: Sequence<String>): Sequence<String> {
        return refs.map { removePages.replace(it, "") }
    }

    private fun findAllRanges(refs: Sequence<String>): Pair<Sequence<String>, List<IntRange>> {
        val ranges = mutableListOf<IntRange>()
        return refs.map {
            val r = removeAndMatchRanges.findAll(it)
            for (matchResult in r) {
                ranges += matchResult.groups[1]!!.value.toInt()..matchResult.groups[2]!!.value.toInt()
            }
            removeAndMatchRanges.replace(it, "")
        } to ranges
    }

    private fun findAllReferences(refs: List<String>): List<Int> {
        return refs.flatMap { line -> matchReference.findAll(line).map { it.groups[1]!!.value.toInt() } }
    }

    @HandlerFactory(Text::class, UrFUConfiguration::class)
    companion object: Factory<TextHandler> {
        override fun create() = TextHandler()
    }
}