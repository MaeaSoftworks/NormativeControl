package com.maeasoftworks.polonium.model

import org.docx4j.wml.Tbl

/**
 * docx4j table wrapper
 *
 * @constructor creates Table by p-index and `Tbl`
 * @param p table's paragraph pointer
 * @param table table
 *
 * @author prmncr
 */
@Suppress("unused")
data class Table(val p: Int, val table: Tbl) {
    var num: Int? = null
}