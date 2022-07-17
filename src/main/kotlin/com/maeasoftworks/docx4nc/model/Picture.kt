package com.maeasoftworks.docx4nc.model

import org.docx4j.wml.Drawing

/**
 * Representation of pictures in document
 * @param p index in p-layer
 * @param r index in r-layer
 * @param c index in c-layer
 * @param drawing picture from document
 * @author prmncr
 */
data class Picture(val p: Int, val r: Int, val c: Int, val drawing: Drawing) {
    /**
     * Picture title
     * @author prmncr
     */
    var title: String? = null
}
