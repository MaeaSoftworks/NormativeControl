package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import kotlin.coroutines.coroutineContext

suspend inline fun <T> P.getPropertyValue(path: PPr.() -> T?): T? {
    return getContext()?.resolver?.getActualProperty(this, path)
}

suspend inline fun <T> R.getPropertyValue(path: RPr.() -> T?): T? {
    return getContext()?.resolver?.getActualProperty(this, path)
}