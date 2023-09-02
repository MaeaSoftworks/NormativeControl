package core.utils

import core.model.Context
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr


inline fun <T> P.getPropertyValue(context: Context, path: PPr.() -> T?): T? {
    return context.resolver.getActualProperty(this, path)
}

inline fun <T> R.getPropertyValue(context: Context, path: RPr.() -> T?): T? {
    return context.resolver.getActualProperty(this, path)
}