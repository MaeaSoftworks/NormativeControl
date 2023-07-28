package com.maeasoftworks.core.utils

import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.PPr

typealias PFunctions = Iterable<PFunction>

/**
 * Runs all checks from PFunctions and adds all mistakes to [DocumentParser.mistakes]
 */
fun PFunctions.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, pPr, isEmpty, root)) }
}