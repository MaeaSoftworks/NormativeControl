package com.maeasoftworks.normativecontrol.parser.model

import com.maeasoftworks.normativecontrol.parser.enums.MistakeType

class MistakeData(
    val mistakeType: MistakeType,
    val p: Int? = null,
    val r: Int? = null,
    val description: String? = null
)