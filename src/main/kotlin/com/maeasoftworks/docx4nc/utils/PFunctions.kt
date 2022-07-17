package com.maeasoftworks.docx4nc.utils

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.wml.PPr

typealias PFunctions = Iterable<PFunction>

/**
 * Применяют каждую функцию из iterable, добавляя результат её выполнения в список ошибок
 *
 * @author prmncr
 */
fun PFunctions.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it(p, pPr, isEmpty, root.doc)) }
}