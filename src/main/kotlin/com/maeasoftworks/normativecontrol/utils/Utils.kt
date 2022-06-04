package com.maeasoftworks.normativecontrol.utils

import org.springframework.core.io.ByteArrayResource

fun createNullableByteArrayResource(data: ByteArray?): ByteArrayResource? =
    if (data == null) null else ByteArrayResource(data)