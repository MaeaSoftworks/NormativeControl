package ru.maeasoftworks.normativecontrol.core

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.jetbrains.annotations.Blocking
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

    @Blocking
    fun load(stream: InputStream) {
        mlPackage = WordprocessingMLPackage.load(stream)
        ctx.load(mlPackage)
    }

    fun runVerification() = with(ctx) {
        mainLoop { pos ->
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
                val start = System.currentTimeMillis()
                val initialized = Reflections("ru.maeasoftworks.normativecontrol.core").getTypesAnnotatedWith(EagerInitialization::class.java)
                initialized.forEach {
                    it.kotlin.objectInstance
                }
                val end = System.currentTimeMillis()
                logger.info("Loaded handlers: [${initialized.joinToString { it.simpleName }}]")
                logger.info("Loading was done in ${end - start} ms")
            }
        }
    }
}