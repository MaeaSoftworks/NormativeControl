package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import org.docx4j.w14.STLigatures

object FontFamily : Property<String>(
    converter = { it }
)

object FontSize : IntProperty("px", 2)

object FontStyle : Property<Boolean?>(
    converter = {
        if (it == null) null else {
            if (it) "italic" else null
        }
    }
)

object FontWeight : Property<Boolean?>(
    converter = {
        if (it == null) null else {
            if (it) "bold" else null
        }
    }
)

object FontVariantCaps : Property<Boolean?>(
    converter = {
        if (it == null) null else {
            if (it) "small-caps" else null
        }
    }
)

object FontVariantLigatures : Property<STLigatures>(
    converter = {
        when (it) {
            STLigatures.NONE -> "none"
            STLigatures.STANDARD -> "common-ligatures"
            STLigatures.CONTEXTUAL -> "contextual"
            STLigatures.HISTORICAL -> "historical-ligatures"
            STLigatures.DISCRETIONAL -> "discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL -> "common-ligatures contextual"
            STLigatures.STANDARD_HISTORICAL -> "common-ligatures historical-ligatures"
            STLigatures.CONTEXTUAL_HISTORICAL -> "contextual historical-ligatures"
            STLigatures.STANDARD_DISCRETIONAL -> "common-ligatures discretionary-ligatures"
            STLigatures.CONTEXTUAL_DISCRETIONAL -> "contextual discretionary-ligatures"
            STLigatures.HISTORICAL_DISCRETIONAL -> "historical-ligatures discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_HISTORICAL -> "no-discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_DISCRETIONAL -> "no-historical-ligatures"
            STLigatures.STANDARD_HISTORICAL_DISCRETIONAL -> "no-contextual"
            STLigatures.CONTEXTUAL_HISTORICAL_DISCRETIONAL -> "no-common-ligatures"
            STLigatures.ALL -> null
            null -> null
        }
    }
)
