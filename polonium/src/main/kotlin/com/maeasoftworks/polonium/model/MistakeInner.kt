package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.MistakeType


/**
 * Mistake representation without `mistakeId` for internal using.
 *
 * If you need `mistakeId`, see [MistakeOuter][com.maeasoftworks.polonium.model.MistakeOuter]
 * @param mistakeType mistake type
 * @param p index of mistake on p-layer
 * @param r i ndex of mistake on r-layer
 * @param description mistake description, in Russian.
 * @see com.maeasoftworks.polonium.samples.DocumentSample
 * @author prmncr
 */
data class MistakeInner(
    val mistakeType: MistakeType,
    val p: Int? = null,
    val r: Int? = null,
    val description: String? = null
)
