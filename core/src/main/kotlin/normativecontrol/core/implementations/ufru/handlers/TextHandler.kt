package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.html.span
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.globalState
import org.docx4j.TextUtils
import org.docx4j.wml.Text

@Handler(Text::class, UrFUConfiguration::class)
object TextHandler : AbstractHandler() {
    private val inBrackets = """\[(.*?)]""".toRegex()
    private val removePages = """,\s*С\.(?:.*)*""".toRegex()
    private val removeAndMatchRanges = """(\d+)\s*-\s*(\d+)""".toRegex()
    private val matchReference = """(\d+)""".toRegex()

    context(VerificationContext)
    override fun handle(element: Any) {
        element as Text
        val rawText = TextUtils.getText(element)

        globalState.referencesInText.addAll(getAllReferences(rawText))
        render append span {
            content = rawText.replace("<", "&lt;").replace(">", "&gt;")
        }
    }

    fun getAllReferences(text: String): Set<Int> {
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

    fun findAllInBrackets(text: String): Sequence<String> {
        return inBrackets.findAll(text).map { it.groups[1]!!.value }
    }

    fun clearPages(refs: Sequence<String>): Sequence<String> {
        return refs.map { removePages.replace(it, "") }
    }

    fun findAllRanges(refs: Sequence<String>): Pair<Sequence<String>, List<IntRange>> {
        val ranges = mutableListOf<IntRange>()
        return refs.map {
            val r = removeAndMatchRanges.findAll(it)
            for (matchResult in r) {
                ranges += matchResult.groups[1]!!.value.toInt()..matchResult.groups[2]!!.value.toInt()
            }
            removeAndMatchRanges.replace(it, "")
        } to ranges
    }

    fun findAllReferences(refs: List<String>): List<Int> {
        return refs.flatMap { line -> matchReference.findAll(line).map { it.groups[1]!!.value.toInt() } }
    }
}