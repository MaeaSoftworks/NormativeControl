package com.maeasoftworks.docx4nc.tweaks

import com.maeasoftworks.docx4nc.utils.rewriteFields
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

/**
 * Fixed & optimized analog for [PropertyResolver.getEffectivePPr].
 *
 * Instead of default resolver, this function applies styles in other method:
 *
 * 1. Creates empty PPr object (base object).
 * 2. Write fields from document default paragraph properties to base.
 * 3. Overwrite non-null fields from default paragraph style PPr to base.
 * 4. Do it again with `<style name>` PPr.
 * 5. And again with paragraph PPr.
 *
 * Result contains the actual values of all fields in correct order.
 * @param p paragraph whose properties need to find
 * @param mlPackage package which contains paragraph
 * @return actual PPr. Warning: it's live object. Clone before editing!
 * @author prmncr
 */
fun PropertyResolver.getActualPPr(p: P, mlPackage: WordprocessingMLPackage): PPr {
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

/**
 * Fixed & optimized analog for [PropertyResolver.getEffectiveRPr].
 *
 * Instead of default resolver, this function applies styles in other method:
 *
 * 1. Creates empty RPr object (base object).
 * 2. Write fields from document default run properties to base.
 * 3. Overwrite non-null fields from default paragraph style RPr to base.
 * 4. Do it again with default character style RPr.
 * 5. Again with `<style name>` RPr.
 * 6. And again with run RPr.
 *
 * Result contains the actual values of all fields in correct order.
 * @param r run whose properties need to find
 * @param mlPackage package which contains run
 * @return actual RPr. Warning: it's live object. Clone before editing!
 * @author prmncr
 */
fun PropertyResolver.getActualRPr(r: R, mlPackage: WordprocessingMLPackage): RPr {
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