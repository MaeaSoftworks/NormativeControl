package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

class MistakeOuter(
    val mistakeId: Long,
    val p: Int? = null,
    val r: Int? = null,
    val mistakeType: MistakeType,
    val description: String? = null
)
