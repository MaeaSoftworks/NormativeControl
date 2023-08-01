package com.maeasoftworks.core.utils

import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.P

typealias PFunctions = Iterable<PFunction>

/**
 * Runs all checks from PFunctions and adds all mistakes to [DocumentParser.mistakes]
 */
fun PFunctions.apply(root: DocumentParser, pPos: Int, p: P, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(pPos, p, isEmpty, root)) }
}
