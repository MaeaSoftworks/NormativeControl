package com.maeasoftworks.normativecontrol.utils

import org.springframework.core.io.ByteArrayResource

fun <T> MutableList<T>.smartAdd(item: T?) {
    if (item != null) {
        this.add(item)
    }
}

fun createNullableByteArrayResource(data: ByteArray?): ByteArrayResource? =
    if (data == null) null else ByteArrayResource(data)