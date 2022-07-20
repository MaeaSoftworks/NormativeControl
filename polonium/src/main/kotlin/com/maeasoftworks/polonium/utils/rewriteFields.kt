package com.maeasoftworks.polonium.utils

import org.apache.commons.lang3.reflect.FieldUtils

/**
 * Write values of all Java fields from `source` to `this`
 * @param T fields receiver
 * @param source fields source
 * @author prmncr
 */
inline fun <reified T> T.rewriteFields(source: T) {
    for (field in FieldUtils.getAllFieldsList(T::class.java)) {
        FieldUtils.writeField(
            this,
            field.name,
            FieldUtils.readField(source, field.name, true) ?: continue,
            true
        )
    }
}