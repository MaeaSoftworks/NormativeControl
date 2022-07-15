package com.maeasoftworks.docxrender

import com.maeasoftworks.docxrender.rendering.RenderLauncher
import com.maeasoftworks.docxrender.utils.start
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

const val file = "src/test/resources/general/full test 2"

fun notMain() {
    RenderLauncher(
        start("$file.docx") {
            init()
            return@start this
        }
    ).render(FileOutputStream(File("$file.html")))
}