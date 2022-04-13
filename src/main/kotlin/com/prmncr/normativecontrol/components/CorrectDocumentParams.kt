package com.prmncr.normativecontrol.components

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CorrectDocumentParams {
    @Value("\${document.page.width}")
    var pageWidth: Long = 0

    @Value("\${document.page.height}")
    var pageHeight: Long = 0

    @Value("\${document.page.margin.top}")
    var pageMarginTop = 0.0

    @Value("\${document.page.margin.right}")
    var pageMarginRight = 0.0

    @Value("\${document.page.margin.bottom}")
    var pageMarginBottom = 0.0

    @Value("\${document.page.margin.left}")
    var pageMarginLeft = 0.0
}