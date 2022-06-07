package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType

class MistakeData(
    var mistakeId: Long = 0,
    val p: Int? = null,
    val r: Int? = null,
    val mistakeType: MistakeType,
    val description: String? = null
)
