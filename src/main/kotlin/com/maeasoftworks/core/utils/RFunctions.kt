package com.maeasoftworks.core.utils

import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.R

typealias RFunctions = Iterable<RFunction>

/**
 * Run all checks from RFunctions and add all mistakes to [DocumentParser.mistakes]
 */
fun RFunctions.apply(root: DocumentParser, pPos: Int, rPos: Int, r: R, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(pPos, rPos, r, isEmpty, root)) }
}
