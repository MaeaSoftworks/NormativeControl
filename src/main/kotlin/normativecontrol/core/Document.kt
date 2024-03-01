package normativecontrol.core

import normativecontrol.core.abstractions.Profile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.jetbrains.annotations.Blocking
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.abstractions.schema.Schema
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Document(schema: Schema) {
    private val profile = Profile(schema)
    val ctx = VerificationContext(profile)
    private lateinit var mlPackage: WordprocessingMLPackage

    @Blocking
    fun load(stream: InputStream) {
        mlPackage = WordprocessingMLPackage.load(stream)
        ctx.load(mlPackage)
    }

    fun runVerification() = with(ctx) {
        doc.content.iterate { pos ->
            val element = doc.content[pos]
            val handler = HandlerMapper[profile.name, element]
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
                val packageName = this::class.java.`package`.name
                logger.info("Searching handlers at '$packageName'...")
                val start = System.currentTimeMillis()
                val initialized = Reflections(packageName).getTypesAnnotatedWith(EagerInitialization::class.java)
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