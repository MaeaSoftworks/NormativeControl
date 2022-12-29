package com.maeasoftworks.livermorium

import com.maeasoftworks.livermorium.rendering.RenderLauncher
import com.maeasoftworks.polonium.model.DocumentData
import com.maeasoftworks.polonium.parsers.DocumentParser
import java.io.File
import java.io.FileOutputStream

const val file = "polonium/src/test/resources/general/full test 2"

/**
 * Test entry point for livermorium. Not used in Normative Control project.
 */
fun main() {
    RenderLauncher(
        DocumentParser(DocumentData(File("$file.docx").readBytes()), "0").apply { runVerification() }
    ).render(FileOutputStream(File("$file.html")))
}
