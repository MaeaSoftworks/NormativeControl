package com.maeasoftworks.normativecontrolcore.core.utils

import com.maeasoftworks.normativecontrolcore.core.model.Mistake
import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import org.docx4j.wml.P
import org.docx4j.wml.PPr

typealias PFunction = (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser) -> Mistake?

object PFunctionFactory {
    inline fun <T> create(
        crossinline path: PPr.() -> T,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser, T?) -> Mistake?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 d: DocumentParser ->
            body(pPos, p, isEmpty, d, p.getPropertyValue(path))
        }
    }

    inline fun <T1, T2> create(
        crossinline path1: PPr.() -> T1,
        crossinline path2: PPr.() -> T2,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser, T1?, T2?) -> Mistake?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 d: DocumentParser ->
            body(pPos, p, isEmpty, d, p.getPropertyValue(path1), p.getPropertyValue(path2))
        }
    }
}