package com.maeasoftworks.docxrender

import com.maeasoftworks.docx4nc.model.DocumentData
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import java.io.File

fun start(path: String, body: DocumentParser.() -> DocumentParser): DocumentParser {
    return body(DocumentParser(DocumentData(File(path).readBytes()), "pass"))
}