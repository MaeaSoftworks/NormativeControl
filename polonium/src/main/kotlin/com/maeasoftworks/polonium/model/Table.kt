package com.maeasoftworks.polonium.model

import org.docx4j.wml.Tbl

/**
 * Класс, содержащий данные о таблице в документе
 *
 * @constructor
 * @param p Номер параграфа, в котором находится таблица
 * @param table Объект таблицы
 *
 * @author prmncr
 */
@Suppress("unused")
data class Table(val p: Int, val table: Tbl) {
    var num: Int? = null
}
