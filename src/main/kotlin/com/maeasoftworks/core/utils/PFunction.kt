package com.maeasoftworks.core.utils

import com.maeasoftworks.core.model.MistakeInner
import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.P
import org.docx4j.wml.PPr

/**
 * Alias for signature of paragraph checking functions in [Rules][com.maeasoftworks.core.model.Rules]
 */
typealias PFunction = (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser) -> MistakeInner?

object PFunctionFactory {
    inline fun <T> create(
        crossinline path: PPr.() -> T,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser, T?) -> MistakeInner?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 d: DocumentParser ->
            body(pPos, p, isEmpty, d, d.resolver.getActualProperty(p, path))
        }
    }

    inline fun <T1, T2> create(
        crossinline path1: PPr.() -> T1,
        crossinline path2: PPr.() -> T2,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, d: DocumentParser, T1?, T2?) -> MistakeInner?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 d: DocumentParser ->
            body(pPos, p, isEmpty, d, d.resolver.getActualProperty(p, path1), d.resolver.getActualProperty(p, path2))
        }
    }
}