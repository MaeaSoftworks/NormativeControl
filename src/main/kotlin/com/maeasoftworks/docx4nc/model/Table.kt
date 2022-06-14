package com.maeasoftworks.docx4nc.model

import org.docx4j.wml.Tbl

data class Table(val p: Int, val table: Tbl) {
    var num: Int? = null
}
