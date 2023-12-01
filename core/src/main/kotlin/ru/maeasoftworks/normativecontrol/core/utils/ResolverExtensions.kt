package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import ru.maeasoftworks.normativecontrol.core.model.Context

inline fun <T> P.getPropertyValue(context: Context, path: PPr.() -> T?): T? {
    return context.resolver.getActualProperty(this, path)
}

inline fun <T> R.getPropertyValue(context: Context, path: RPr.() -> T?): T? {
    return context.resolver.getActualProperty(this, path)
}