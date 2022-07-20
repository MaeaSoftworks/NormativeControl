package com.maeasoftworks.polonium.utils

import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.wml.RPr

typealias RFunctions = Iterable<RFunction>

/**
 * Run all checks from RFunctions and add all mistakes to `DocumentParser.mistakes`
 * @author prmncr
 */
fun RFunctions.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, r, rPr, isEmpty, root)) }
}
