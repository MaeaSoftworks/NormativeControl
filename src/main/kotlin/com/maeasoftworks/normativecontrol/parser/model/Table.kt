package com.maeasoftworks.normativecontrol.parser.model

import org.docx4j.wml.Tbl

class Table(val p: Int, val table: Tbl) {
    var num: Int? = null
}