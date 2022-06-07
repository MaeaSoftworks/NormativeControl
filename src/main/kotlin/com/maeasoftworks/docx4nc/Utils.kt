package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.model.MistakeBody
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

typealias PFunction = (Int, PPr, Boolean, MainDocumentPart) -> MistakeBody?

typealias RFunction = (Int, Int, RPr, Boolean, MainDocumentPart) -> MistakeBody?

fun Iterable<PFunction>.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, pPr, isEmpty, root.mainDocumentPart)) }
}

fun Iterable<RFunction>.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, r, rPr, isEmpty, root.mainDocumentPart)) }
}
