package com.maeasoftworks.docx4nc.utils

import org.apache.commons.lang3.reflect.FieldUtils

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