package normativecontrol.core.rendering.html

import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.rendering.css.DeclarationBlock
import org.docx4j.wml.SectPr

/**
 * Utility class for document pagination.
 */
object Pages {
    /**
     * Creates new CSS page style based on docx [sectPr].
     * @param sectPr section properties
     * @return class for selector of created CSS style.
     */
    context(RenderingContext)
    fun createPageStyle(sectPr: SectPr?): String {
        val styleId = "page${pageStyleId++}"

        val declarationBlock = DeclarationBlock(noInline = true)
        declarationBlock += {
            width set sectPr?.pgSz?.w?.toDouble()
            minHeight set sectPr?.pgSz?.h?.toDouble()

            paddingTop set sectPr?.pgMar?.top?.toDouble()
            paddingLeft set sectPr?.pgMar?.left?.toDouble()
            paddingBottom set sectPr?.pgMar?.bottom?.toDouble()
            paddingRight set sectPr?.pgMar?.right?.toDouble()
        }
        externalGlobalStylesheet.rulesets[".$styleId"] = declarationBlock
        return styleId
    }
}