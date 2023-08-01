package com.maeasoftworks.core.utils

import com.maeasoftworks.core.enums.MistakeType
import com.maeasoftworks.core.model.MistakeInner
import com.maeasoftworks.core.parsers.DocumentParser
import org.docx4j.wml.R
import org.docx4j.wml.RPr

/**
 * Alias for signature of run checking functions in [Rules][com.maeasoftworks.core.model.Rules]
 */
typealias RFunction = (pPos: Int, rPos: Int, r: R, isEmpty: Boolean, d: DocumentParser) -> MistakeInner?

inline fun <T> createRFunction(
    crossinline path: RPr.() -> T,
    mistakeType: MistakeType,
    crossinline mistakeCondition: (r: R, isEmpty: Boolean, d: DocumentParser, T?) -> Boolean,
    crossinline mistakeDescription: (T?) -> String? = { null }
): RFunction {
    return { pPos: Int, rPos: Int, r: R, isEmpty: Boolean, d: DocumentParser ->
        val t = d.resolver.getActualProperty(r, path)
        if (mistakeCondition(r, isEmpty, d, t)) {
            MistakeInner(mistakeType, pPos, rPos, description = mistakeDescription(t))
        } else null
    }
}