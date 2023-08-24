package com.maeasoftworks.normativecontrolcore.core.parsers

import com.maeasoftworks.normativecontrolcore.core.context.Context
import com.maeasoftworks.normativecontrolcore.core.model.DocumentChildParsers
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DocumentParser(stream: InputStream) {
    private val mlPackage: WordprocessingMLPackage = WordprocessingMLPackage.load(stream)
    private val ctx = Context(mlPackage)
    val doc: MainDocumentPart = mlPackage.mainDocumentPart.also { it.styleDefinitionsPart.jaxbElement }
    val autoHyphenation: Boolean? by lazy { doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal }

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