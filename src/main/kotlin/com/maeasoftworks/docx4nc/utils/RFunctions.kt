package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.wml.RPr

typealias RFunctions = Iterable<RFunction>

/**
 * Применяют каждую функцию из iterable, добавляя результат её выполнения в список ошибок
 *
 * @author prmncr
 */
fun RFunctions.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, r, rPr, isEmpty, root)) }
}
