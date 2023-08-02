package com.maeasoftworks.normativecontrolcore.core.model

import org.docx4j.wml.Drawing

/**
 * Representation of pictures in document
 * @constructor creates Picture by all indexes and `Drawing`
 * @param p index in p-layer
 * @param r index in r-layer
 * @param c index in c-layer
 * @param drawing picture from document
 */
data class Picture(val p: Int, val r: Int, val c: Int, val drawing: Drawing) {
    var title: String? = null
}
