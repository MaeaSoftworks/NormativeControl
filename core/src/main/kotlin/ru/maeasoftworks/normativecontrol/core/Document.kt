package ru.maeasoftworks.normativecontrol.core

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import ru.maeasoftworks.normativecontrol.core.abstractions.ChapterHeader
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
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

    companion object {
        private var isLoaded = false
        private val logger = LoggerFactory.getLogger(Document::class.java)

        init {
            if (!isLoaded) {
                isLoaded = true
                val initialized = Reflections("ru.maeasoftworks.normativecontrol.core").getTypesAnnotatedWith(EagerInitialization::class.java)
                initialized.forEach {
                    it.kotlin.objectInstance
                }
                logger.debug("Loaded handlers: [${initialized.joinToString { it.simpleName }}]")
            }
        }
    }
}