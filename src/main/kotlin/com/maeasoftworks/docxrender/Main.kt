package com.maeasoftworks.docxrender

import com.maeasoftworks.docxrender.rendering.RenderLauncher
import com.maeasoftworks.docxrender.utils.start
import java.io.File
import java.io.FileOutputStream

const val file = "src/test/resources/general/full test 2"

// rename from main before pushing
fun notMain() {
    RenderLauncher(
        start("$file.docx") {
            init()
            runVerification()
            return@start this
        }
    ).render(FileOutputStream(File("$file.html")))
}