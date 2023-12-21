package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.R
import org.docx4j.wml.RPr
import ru.maeasoftworks.normativecontrol.core.enums.Closure
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

@Deprecated("Use new API")
typealias RFunction = suspend (r: R, isEmpty: Boolean, ctx: VerificationContext) -> Mistake?

@Deprecated("Use new API")
inline fun <T> createRFunction(
    crossinline valueProvider: RPr.() -> T,
    mistakeType: MistakeType,
    crossinline mistakeCondition: (r: R, isEmpty: Boolean, ctx: VerificationContext, T?) -> Boolean,
    crossinline mistakeActual: (T?) -> String? = { null },
    mistakeExpected: String? = null,
    closure: Closure = Closure.R
): RFunction {
    return { r: R, isEmpty: Boolean, ctx: VerificationContext ->
        val t = r.getPropertyValue(valueProvider)
        if (mistakeCondition(r, isEmpty, ctx, t)) {
            Mistake(mistakeType, Closure.R, mistakeActual(t), mistakeExpected)
        } else {
            null
        }
    }
}