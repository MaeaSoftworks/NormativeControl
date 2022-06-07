package com.maeasoftworks.normativecontrol.utils

import com.maeasoftworks.docx4nc.model.MistakeData
import com.maeasoftworks.normativecontrol.dao.Mistake
import org.springframework.core.io.ByteArrayResource

fun createNullableByteArrayResource(data: ByteArray?): ByteArrayResource? =
    if (data == null) null else ByteArrayResource(data)

fun MistakeData.toDto(documentId: String): Mistake {
    return Mistake(documentId, this.mistakeId, this.p, this.r, this.mistakeType, this.description)
}
