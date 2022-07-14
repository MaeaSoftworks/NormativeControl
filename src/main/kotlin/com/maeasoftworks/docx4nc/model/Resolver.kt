package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.apache.commons.lang3.reflect.FieldUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import org.jvnet.jaxb2_commons.ppp.Child

class Resolver(private val resolver: PropertyResolver, private val root: DocumentParser) {
    private var pPrs: MutableMap<String, PPr> = HashMap()
    private var rPrs: MutableMap<R, RPr?> = HashMap()

    fun getEffectivePPr(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getEffectivePPr(p.pPr)
            //.apply { // fix lost properties
            //    this.suppressAutoHyphens = p.pPr?.suppressAutoHyphens
            //    this.parent = p
            //}
            .also { pPrs[p.paraId] = it }
    }

    @Deprecated("Use getBetterEffectiveRPr")
    fun getEffectiveRPr(r: R): RPr {
        return rPrs[r] ?: resolver.getEffectiveRPr(
            r.rPr,
            pPrs[(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P).paraId]
                ?: getEffectivePPr(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P)
        )
            //.apply {  // fix lost properties
            //    this.parent = r
            //}
            .also { rPrs[r] = it }
    }

    fun getBetterEffectiveRPr(r: R): RPr {
        val rPr: RPr? = r.rPr
        val rStyleRPr = root.doc.styleDefinitionsPart.getStyleById(r.rPr?.rStyle?.`val`)?.rPr
        val paraRPr = if (r.parent is P) (r.parent as P).pPr?.rPr else null
        val paraPStyleRPr = if (r.parent is P) root.doc.styleDefinitionsPart.getStyleById("${(r.parent as P).pPr?.pStyle?.`val`}Char")?.rPr else null
        val resolverDefaultRPR = resolver.documentDefaultRPr
        val defaultRPr = root.doc.styleDefinitionsPart.defaultParagraphStyle.rPr
        val result = RPr()
        val localRPrs = mutableListOf(defaultRPr, resolverDefaultRPR, paraPStyleRPr, paraRPr, rStyleRPr, rPr)
        for (rpr in localRPrs.filterNotNull()) {
            for (field in FieldUtils.getAllFieldsList(RPr::class.java)) {
                val actual = FieldUtils.readField(rpr, field.name, true) ?: continue
                FieldUtils.writeField(result, field.name, actual, true)
            }
        }
        rPrs[r] = result
        return result
    }
}
