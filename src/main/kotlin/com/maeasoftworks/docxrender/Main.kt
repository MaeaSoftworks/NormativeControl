package com.maeasoftworks.docxrender

import com.maeasoftworks.docx4nc.parsers.AnonymousParser
import com.maeasoftworks.docxrender.rendering.RenderLauncher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

val log: Logger = LoggerFactory.getLogger("MainKt")
const val file = "src/test/resources/general/full test 2"

fun main() {
    RenderLauncher (
        start("$file.docx") {
            init()
            verifyPageSize()
            verifyPageMargins()
            setupChapters()
            createParsers()
            for (parser in parsers) {
                parser.parse()
            }
            checkPicturesOrder(AnonymousParser(this), 0, true, pictures)
            mistakes.forEach {
                log.info("[p {} r {}] {}: {}", it.p, it.r, it.mistakeType, it.description)
            }
            return@start this
        }
    ).render(FileOutputStream(File("$file.html")))
}