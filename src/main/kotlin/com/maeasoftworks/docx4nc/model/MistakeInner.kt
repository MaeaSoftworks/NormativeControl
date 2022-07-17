package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType


/**
 * Mistake representation without `mistakeId` for internal using.
 *
 * If you need `mistakeId`, see [MistakeOuter][com.maeasoftworks.docx4nc.model.MistakeOuter]
 * @author prmncr
 */
data class MistakeInner(
    /**
     * Mistake type
     * @see com.maeasoftworks.docx4nc.enums.MistakeType
     * @author prmncr
     */
    val mistakeType: MistakeType,

    /**
     * Index of mistake on p-layer
     * @see com.maeasoftworks.docx4nc.samples.Philosophy_of_Layers
     * @author prmncr
     */
    val p: Int? = null,

    /**
     * Index of mistake on r-layer
     * @see com.maeasoftworks.docx4nc.samples.Philosophy_of_Layers
     * @author prmncr
     */
    val r: Int? = null,

    /**
     * Mistake description. Unfortunately, in Russian.
     *
     * @author prmncr
     */
    val description: String? = null
)
