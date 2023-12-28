package ru.maeasoftworks.normativecontrol.core

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.abstractions.ChapterHeader
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Document(val ctx: VerificationContext) {
    private lateinit var mlPackage: WordprocessingMLPackage
    private lateinit var doc: MainDocumentPart

    fun load(stream: InputStream) {
        mlPackage = WordprocessingMLPackage.load(stream)
        doc = mlPackage.mainDocumentPart.also { it.styleDefinitionsPart.jaxbElement }
        ctx.load(mlPackage)
    }

    suspend fun runVerification() {
        ctx.ptr.mainLoop { pos ->
            val element = doc.content[pos]
            val handler = HandlerMapper[ctx.profile, element]
            if (handler != null) {
                if (handler is ChapterHeader) {
                    if (handler.isHeader(element)) {
                        val chapter = handler.detectChapterByHeader(element)
                        handler.checkChapterOrderAndUpdateContext(chapter)
                    }
                }
                handler.handle(element)
            }
        }
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }
}