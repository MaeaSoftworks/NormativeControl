package com.maeasoftworks.docx4nc.tweaks

import com.maeasoftworks.docx4nc.utils.rewriteFields
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

fun PropertyResolver.getEffectivePPr(p: P, mlPackage: WordprocessingMLPackage): PPr {
    return PPr().also {
        mutableListOf(
            documentDefaultPPr,
            mlPackage.mainDocumentPart.styleDefinitionsPart.defaultParagraphStyle.pPr,
            mlPackage.mainDocumentPart.styleDefinitionsPart.getStyleById(p.pPr?.pStyle?.`val`)?.pPr,
            p.pPr
        ).filterNotNull().forEach { ppr ->
            it.rewriteFields(ppr)
        }
    }
}

fun PropertyResolver.getEffectiveRPr(r: R, mlPackage: WordprocessingMLPackage): RPr {
    return RPr().also {
        mutableListOf(
            documentDefaultRPr,
            mlPackage.mainDocumentPart.styleDefinitionsPart.defaultParagraphStyle.rPr,
            mlPackage.mainDocumentPart.styleDefinitionsPart.defaultCharacterStyle.rPr,
            if (r.parent is P) mlPackage.mainDocumentPart.styleDefinitionsPart.getStyleById("${(r.parent as P).pPr?.pStyle?.`val`}Char")?.rPr else null,
            mlPackage.mainDocumentPart.styleDefinitionsPart.getStyleById(r.rPr?.rStyle?.`val`)?.rPr,
            r.rPr
        ).filterNotNull().forEach { rpr ->
            it.rewriteFields(rpr)
        }
    }
}