package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.PPr
import org.docx4j.wml.RPr
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

context(VerificationContext)
inline fun <T> PPr?.getPropertyValue(path: PPr.() -> T?): T? {
    return resolver.getActualProperty(this, path)
}

context(VerificationContext)
inline fun <T> RPr?.getPropertyValue(path: RPr.() -> T?): T? {
    return resolver.getActualProperty(this, path)
}