package com.maeasoftworks.docx4nc.model

import org.docx4j.wml.Tbl
/**
 * Класс, содержащий данные о таблице в одкументе
 *
 * @constructor
 * @param p Номер параграфа, в котором находится таблица
 * @param Tbl Объект таблицы
 *
 * @author prmncr
 */
data class Table(val p: Int, val table: Tbl) {
    var num: Int? = null
}
