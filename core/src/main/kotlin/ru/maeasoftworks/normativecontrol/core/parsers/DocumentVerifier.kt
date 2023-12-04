package ru.maeasoftworks.normativecontrol.core.parsers

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.jvnet.jaxb2_commons.ppp.Child
import ru.maeasoftworks.normativecontrol.core.model.Transmission
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

class DocumentVerifier(val ctx: VerificationContext) {
    private lateinit var mlPackage: WordprocessingMLPackage
    lateinit var doc: MainDocumentPart
    var autoHyphenation: Boolean? = null

    fun load(stream: InputStream) {
        mlPackage = WordprocessingMLPackage.load(stream)
        doc = mlPackage.mainDocumentPart.also { it.styleDefinitionsPart.jaxbElement }
        autoHyphenation = doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal
        ctx.load(mlPackage)
    }

    suspend fun runVerification() {
        ctx.ptr.mainLoop { pos ->
            val currentChild = doc.content[pos] as? Child
            Transmission.transmitChild(currentChild)
        }
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }
}