package com.maeasoftworks.rendering.model.css.properties

import org.docx4j.w14.STLigatures
import org.docx4j.wml.RFonts

object FontFamily : Property(
    converter = { set ->
        val fontSet = set as RFonts?
        val fonts = listOfNotNull(fontSet?.ascii, fontSet?.cs, fontSet?.eastAsia, fontSet?.hAnsi)
        if (fonts.isNotEmpty()) {
            if (fonts.drop(1).all { it == fonts[0] }) fonts[0] else null
        } else {
            if (fontSet == null) null else fonts.joinToString(",")
        }
    }
)

object FontSize : Property("px", coefficient = 2)

object FontStyle : Property(
    converter = { value ->
        (value as Boolean?).let {
            if (it == null) null else {
                if (it) "italic" else null
            }
        }
    }
)

object FontWeight : Property(
    converter = { value ->
        (value as Boolean?).let {
            if (it == null) null else {
                if (it) "bold" else null
            }
        }
    }
)

object FontVariantCaps : Property(
    converter = { value ->
        (value as Boolean?).let {
            if (it == null) null else {
                if (it) "small-caps" else null
            }
        }
    }
)

object FontVariantLigatures : Property(
    converter = {
        when (it as STLigatures) {
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
        }
    }
)
