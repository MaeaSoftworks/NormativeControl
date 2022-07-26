package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.MistakeType


/**
 * Mistake representation without `mistakeId` for internal using.
 *
 * If you need `mistakeId`, see [MistakeOuter][com.maeasoftworks.polonium.model.MistakeOuter]
 * @author prmncr
 */
data class MistakeInner(
    /**
     * Mistake type
     * @see com.maeasoftworks.polonium.enums.MistakeType
     * @author prmncr
     */
    val mistakeType: MistakeType,

    /**
     * Index of mistake on p-layer
     * @see com.maeasoftworks.polonium.samples.DocumentSample
     * @author prmncr
     */
    val p: Int? = null,

    /**
     * Index of mistake on r-layer
     * @see com.maeasoftworks.polonium.samples.DocumentSample
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
