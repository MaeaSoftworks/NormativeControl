package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.R
import org.docx4j.wml.RPr
import ru.maeasoftworks.normativecontrol.core.enums.CaptureType
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.model.Context
import ru.maeasoftworks.normativecontrol.core.model.Mistake

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
        } else {
            null
        }
    }
}
