package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

class MistakeInner(
    val mistakeType: MistakeType,
    val p: Int? = null,
    val r: Int? = null,
    val description: String? = null
)
