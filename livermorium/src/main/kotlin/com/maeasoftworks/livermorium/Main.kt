package com.maeasoftworks.livermorium

import com.maeasoftworks.livermorium.rendering.RenderLauncher
import com.maeasoftworks.livermorium.utils.start
import java.io.File
import java.io.FileOutputStream

const val file = "polonium/src/test/resources/general/full test 2"

fun main() {
    RenderLauncher(
        start("$file.docx") {
            runVerification()
            return@start this
        }
    ).render(FileOutputStream(File("$file.html")))
}