package normativecontrol.core.rendering.html

import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.rendering.css.Style
import org.docx4j.wml.SectPr

context(RenderingContext)
fun createPageStyle(sectPr: SectPr?): String {
    val styleId = "page${pageStyleId++}"

    val style = Style(noInline = true)
    style += {
        width set sectPr?.pgSz?.w?.toDouble()
        minHeight set sectPr?.pgSz?.h?.toDouble()

        paddingTop set sectPr?.pgMar?.top?.toDouble()
        paddingLeft set sectPr?.pgMar?.left?.toDouble()
        paddingBottom set sectPr?.pgMar?.bottom?.toDouble()
        paddingRight set sectPr?.pgMar?.right?.toDouble()
    }
    externalGlobalStylesheet.styles[".$styleId"] = style
    return styleId
}