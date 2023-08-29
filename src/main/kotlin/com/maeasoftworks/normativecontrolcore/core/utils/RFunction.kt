package com.maeasoftworks.normativecontrolcore.core.utils

import com.maeasoftworks.normativecontrolcore.core.context.Context
import com.maeasoftworks.normativecontrolcore.core.enums.CaptureType
import com.maeasoftworks.normativecontrolcore.core.enums.MistakeType
import com.maeasoftworks.normativecontrolcore.core.model.Mistake
import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import org.docx4j.wml.R
import org.docx4j.wml.RPr

typealias RFunction = (r: R, isEmpty: Boolean, ctx: Context) -> Mistake?

inline fun <T> createRFunction(
    crossinline valueProvider: RPr.() -> T,
    mistakeType: MistakeType,
    crossinline mistakeCondition: (r: R, isEmpty: Boolean, ctx: Context, T?) -> Boolean,
    crossinline mistakeActual: (T?) -> String? = { null },
    mistakeExpected: String? = null,
    captureType: CaptureType = CaptureType.R
): RFunction {
    return { r: R, isEmpty: Boolean, ctx: Context ->
        val t = r.getPropertyValue(ctx, valueProvider)
        if (mistakeCondition(r, isEmpty, ctx, t)) {
            Mistake(mistakeType, CaptureType.R, mistakeActual(t), mistakeExpected)
        } else null
    }
}