package ru.maeasoftworks.normativecontrol.core.html

import org.docx4j.wml.SectPr
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.RuntimeState
import ru.maeasoftworks.normativecontrol.core.css.Style

context(VerificationContext)
fun createPageStyle(sectPr: SectPr?): String {
    val state = getSharedStateAs<RuntimeState>()
    val styleId = "page${state.pageStyleId++}"

    val style = Style(noInline = true)
    style += {
        width set sectPr?.pgSz?.w?.toDouble()
        minHeight set sectPr?.pgSz?.h?.toDouble()

        paddingTop set sectPr?.pgMar?.top?.toDouble()
        paddingLeft set sectPr?.pgMar?.left?.toDouble()
        paddingBottom set sectPr?.pgMar?.bottom?.toDouble()
        paddingRight set sectPr?.pgMar?.right?.toDouble()
    }
    state.externalGlobalStylesheet.styles[".$styleId"] = style
    return styleId
}