package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

fun Iterable<PFunction>.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, pPr, isEmpty, root.doc)) }
}

fun Iterable<RFunction>.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, r, rPr, isEmpty, root.doc)) }
}
