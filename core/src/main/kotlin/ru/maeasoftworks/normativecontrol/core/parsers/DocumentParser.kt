package ru.maeasoftworks.normativecontrol.core.parsers

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.model.Context
import ru.maeasoftworks.normativecontrol.core.model.DocumentChildParsers
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DocumentParser {
    private lateinit var mlPackage: WordprocessingMLPackage
    val ctx = Context()
    lateinit var doc: MainDocumentPart
    var autoHyphenation: Boolean? = null

    fun load(stream: InputStream) {
        mlPackage = WordprocessingMLPackage.load(stream)
        doc = mlPackage.mainDocumentPart.also { it.styleDefinitionsPart.jaxbElement }
        autoHyphenation = doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal
        ctx.load(mlPackage)
    }

    suspend fun runVerification() {
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
