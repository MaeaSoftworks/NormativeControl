package com.maeasoftworks.docx4nc.model

import org.docx4j.wml.Drawing

/**
 * Является классом «картинки» из проверяемого файла
 *
 * @author prmncr
 */
data class Picture(val p: Int, val r: Int, val c: Int, val drawing: Drawing) {

    /**
     * Переменная которая обозначает заголовок «картинки»
     *
     * @author prmncr
     */
    var title: String? = null
}
