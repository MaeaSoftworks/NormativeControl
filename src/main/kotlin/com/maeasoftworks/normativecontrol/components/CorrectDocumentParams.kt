package com.maeasoftworks.normativecontrol.components

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class CorrectDocumentParams {
    @Value("\${document.page.width}")
    lateinit var pageWidth: BigInteger

    @Value("\${document.page.height}")
    lateinit var pageHeight: BigInteger

    @Value("\${document.page.margin.top}")
    lateinit var pageMarginTop: BigInteger

    @Value("\${document.page.margin.right}")
    lateinit var pageMarginRight: BigInteger

    @Value("\${document.page.margin.bottom}")
    lateinit var pageMarginBottom: BigInteger

    @Value("\${document.page.margin.left}")
    lateinit var pageMarginLeft: BigInteger
}