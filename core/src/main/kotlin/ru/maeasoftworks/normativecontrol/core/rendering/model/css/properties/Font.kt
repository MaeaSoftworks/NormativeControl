package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

import org.docx4j.w14.STLigatures
import ru.maeasoftworks.normativecontrol.core.rendering.utils.FONT_SCALING

object FontFamily : Property<String>("font-family")

object FontSize : IntProperty("font-size", FONT_SCALING, "px")

object FontStyle : Property<Boolean?>("font-style", {
    if (it == null) {
        null
    } else {
        if (it) "italic" else null
    }
})

object FontWeight : Property<Boolean?>("font-weight", {
    if (it == null) {
        null
    } else {
        if (it) "bold" else null
    }
})

object FontVariantCaps : Property<Boolean?>("font-variant-caps", {
    if (it == null) {
        null
    } else {
        if (it) "small-caps" else null
    }
})

object FontVariantLigatures : Property<STLigatures>(
    "font-variant-ligatures",
    {
        when (it) {
            STLigatures.NONE -> "none"
            STLigatures.STANDARD -> "shared-ligatures"
            STLigatures.CONTEXTUAL -> "contextual"
            STLigatures.HISTORICAL -> "historical-ligatures"
            STLigatures.DISCRETIONAL -> "discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL -> "shared-ligatures contextual"
            STLigatures.STANDARD_HISTORICAL -> "shared-ligatures historical-ligatures"
            STLigatures.CONTEXTUAL_HISTORICAL -> "contextual historical-ligatures"
            STLigatures.STANDARD_DISCRETIONAL -> "shared-ligatures discretionary-ligatures"
            STLigatures.CONTEXTUAL_DISCRETIONAL -> "contextual discretionary-ligatures"
            STLigatures.HISTORICAL_DISCRETIONAL -> "historical-ligatures discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_HISTORICAL -> "no-discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_DISCRETIONAL -> "no-historical-ligatures"
            STLigatures.STANDARD_HISTORICAL_DISCRETIONAL -> "no-contextual"
            STLigatures.CONTEXTUAL_HISTORICAL_DISCRETIONAL -> "no-shared-ligatures"
            STLigatures.ALL -> null
            null -> null
        }
    }
)