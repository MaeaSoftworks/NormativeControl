package com.maeasoftworks.normativecontrolcore.core.utils

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import org.jvnet.jaxb2_commons.ppp.Child

private const val RESOLVER_KEY = "__resolver"

inline fun <T> P.getPropertyValue(path: PPr.() -> T?): T? {
    return this.mainDocumentPart.resolver.getActualProperty(this, path)
}

inline fun <T> R.getPropertyValue(path: RPr.() -> T?): T? {
    return this.mainDocumentPart.resolver.getActualProperty(this, path)
}

val Child.mainDocumentPart: MainDocumentPart
    get() {
    var parent = this.parent
    while (parent !is MainDocumentPart) {
        parent = (parent as Child).parent
    }
    return parent
}

var MainDocumentPart.resolver: PropertyResolver
    set(value) {
        this.contents.parent = this
        this.setUserData(RESOLVER_KEY, value)
    }
    get() {
        return this.getUserData(RESOLVER_KEY) as PropertyResolver
    }