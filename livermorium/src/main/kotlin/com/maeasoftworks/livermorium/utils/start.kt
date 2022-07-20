package com.maeasoftworks.livermorium.utils

import com.maeasoftworks.polonium.model.DocumentData
import com.maeasoftworks.polonium.parsers.DocumentParser
import java.io.File

fun start(path: String, body: DocumentParser.() -> DocumentParser): DocumentParser {
    return body(DocumentParser(DocumentData(File(path).readBytes()), "pass"))
}