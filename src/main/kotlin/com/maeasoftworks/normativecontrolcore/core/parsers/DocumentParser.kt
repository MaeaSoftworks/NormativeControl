package com.maeasoftworks.normativecontrolcore.core.parsers

import com.maeasoftworks.normativecontrolcore.core.context.Context
import com.maeasoftworks.normativecontrolcore.core.model.DocumentChildParsers
import com.maeasoftworks.normativecontrolcore.core.utils.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DocumentParser(byteArrayStream: ByteArrayInputStream) {
    private val mlPackage: WordprocessingMLPackage = WordprocessingMLPackage.load(byteArrayStream)
    val resolver: PropertyResolver = PropertyResolver(mlPackage)
    private val ctx = Context(mlPackage, resolver)
    val doc: MainDocumentPart = mlPackage.mainDocumentPart.also { it.styleDefinitionsPart.jaxbElement }
    val autoHyphenation: Boolean = doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal!!

    fun runVerification() {
        while (ctx.ptr.bodyPosition < ctx.ptr.totalChildSize) {
            val currentChild = doc.content[ctx.ptr.bodyPosition]
            DocumentChildParsers.parseDocumentChild(currentChild, ctx)
            ctx.ptr.moveNextChild()
        }
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }
}
/*
fun verifyPageSize() {
    val pageSize = doc.contents.body.sectPr.pgSz
    if (pageSize.w.intValueExact() != 11906) {
        addMistake(PAGE_WIDTH_IS_INCORRECT)
    }
    if (pageSize.h.intValueExact() != 16838) {
        addMistake(PAGE_HEIGHT_IS_INCORRECT)
    }
}

private fun verifyPageMargins() {
    val pageMargins = doc.contents.body.sectPr.pgMar
    if (pageMargins.top.intValueExact() != 1134) {
        addMistake(PAGE_MARGIN_TOP_IS_INCORRECT)
    }
    if (pageMargins.right.intValueExact() != 850) {
        addMistake(PAGE_MARGIN_RIGHT_IS_INCORRECT)
    }
    if (pageMargins.bottom.intValueExact() != 1134) {
        addMistake(PAGE_MARGIN_BOTTOM_IS_INCORRECT)
    }
    if (pageMargins.left.intValueExact() != 1701) {
        addMistake(PAGE_MARGIN_LEFT_IS_INCORRECT)
    }
}
}
*/