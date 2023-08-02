package com.maeasoftworks.normativecontrolcore.core.utils

import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

inline fun <T> P.getPropertyValue(resolver: PropertyResolver, path: PPr.() -> T?): T? {
    return resolver.getActualProperty(this, path)
}

inline fun <T> R.getPropertyValue(resolver: PropertyResolver, path: RPr.() -> T?): T? {
    return resolver.getActualProperty(this, path)
}