package ru.maeasoftworks.normativecontrol.core.utils

import ru.maeasoftworks.normativecontrol.core.model.Context
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import org.docx4j.wml.P
import org.docx4j.wml.PPr

typealias PFunction = (pPos: Int, p: P, isEmpty: Boolean, ctx: Context) -> Mistake?

object PFunctionFactory {
    inline fun <T> create(
        crossinline path: PPr.() -> T,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, ctx: Context, T?) -> Mistake?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 ctx: Context ->
            body(pPos, p, isEmpty, ctx, p.getPropertyValue(ctx, path))
        }
    }

    inline fun <T1, T2> create(
        crossinline path1: PPr.() -> T1,
        crossinline path2: PPr.() -> T2,
        crossinline body: (pPos: Int, p: P, isEmpty: Boolean, ctx: Context, T1?, T2?) -> Mistake?
    ): PFunction {
        return { pPos: Int,
                 p: P,
                 isEmpty: Boolean,
                 ctx: Context ->
            body(pPos, p, isEmpty, ctx, p.getPropertyValue(ctx, path1), p.getPropertyValue(ctx, path2))
        }
    }
}