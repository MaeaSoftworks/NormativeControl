package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.P
import org.docx4j.wml.PPr
import ru.maeasoftworks.normativecontrol.core.model.Mistake

typealias PFunction = suspend (pPos: Int, p: P, isEmpty: Boolean) -> Mistake?

object PFunctionFactory {
    inline fun <T> create(
        crossinline path: PPr.() -> T,
        crossinline body: suspend (pPos: Int, p: P, isEmpty: Boolean, T?) -> Mistake?
    ): PFunction {
        return { pPos: Int, p: P, isEmpty: Boolean -> body(pPos, p, isEmpty, p.getPropertyValue(path)) }
    }

    inline fun <T1, T2> create(
        crossinline path1: PPr.() -> T1,
        crossinline path2: PPr.() -> T2,
        crossinline body: suspend (pPos: Int, p: P, isEmpty: Boolean, T1?, T2?) -> Mistake?
    ): PFunction {
        return { pPos: Int, p: P, isEmpty: Boolean -> body(pPos, p, isEmpty, p.getPropertyValue(path1), p.getPropertyValue(path2)) }
    }
}