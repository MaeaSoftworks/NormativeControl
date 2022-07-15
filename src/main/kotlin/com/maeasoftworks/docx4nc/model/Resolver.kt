package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.apache.commons.lang3.reflect.FieldUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import org.jvnet.jaxb2_commons.ppp.Child
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Resolver(private val resolver: PropertyResolver, private val root: DocumentParser) {
    private var pPrs: MutableMap<String, PPr> = HashMap()
    private var rPrs: MutableMap<R, RPr?> = HashMap()
    private val stats: MutableMap<String, Int> = mutableMapOf(
        "total" to 0,
        "rPr" to 0,
        "rStyleRPr" to 0,
        "paraRPr" to 0,
        "paraPStyleRPr" to 0,
        "resolverDefaultRPR" to 0,
        "defaultRPr" to 0
    )

    fun getEffectivePPr(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getEffectivePPr(p.pPr)
            .apply { // fix lost properties
                this.suppressAutoHyphens = p.pPr?.suppressAutoHyphens
                this.parent = p
            }
            .also {
                pPrs[p.paraId] = it
            }
    }

    @Deprecated("Use getBetterEffectiveRPr")
    fun getEffectiveRPr(r: R): RPr {
        return rPrs[r] ?: resolver.getEffectiveRPr(
            r.rPr,
            pPrs[(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P).paraId]
                ?: getEffectivePPr(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P)
        ).apply {  // fix lost properties
            this.parent = r
        }.also {
            rPrs[r] = it
        }
    }

    fun getBetterEffectiveRPr(r: R): RPr {
        val rPr: RPr? = r.rPr
        val rStyleRPr = root.doc.styleDefinitionsPart.getStyleById(r.rPr?.rStyle?.`val`)?.rPr
        val paraRPr = if (r.parent is P) (r.parent as P).pPr?.rPr else null
        val paraPStyleRPr =
            if (r.parent is P) root.doc.styleDefinitionsPart.getStyleById("${(r.parent as P).pPr?.pStyle?.`val`}Char")?.rPr else null
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

        stats["total"] = stats["total"]!! + 1
        if (rPr != null) stats["rPr"] = stats["rPr"]!! + 1
        if (rStyleRPr != null) stats["rStyleRPr"] = stats["rStyleRPr"]!! + 1
        if (paraRPr != null) stats["paraRPr"] = stats["paraRPr"]!! + 1
        if (paraPStyleRPr != null) stats["paraPStyleRPr"] = stats["paraPStyleRPr"]!! + 1
        if (resolverDefaultRPR != null) stats["resolverDefaultRPR"] = stats["resolverDefaultRPR"]!! + 1
        if (defaultRPr != null) stats["defaultRPr"] = stats["defaultRPr"]!! + 1

        rPrs[r] = result
        return result
    }

    fun printStats() {
        printStat("total", stats["total"])
        printStat("rPr", stats["rPr"])
        printStat("rStyleRPr", stats["rStyleRPr"])
        printStat("paraRPr", stats["paraRPr"])
        printStat("paraPStyleRPr", stats["paraPStyleRPr"])
        printStat("resolverDefaultRPR", stats["resolverDefaultRPR"])
        printStat("defaultRPr", stats["defaultRPr"])
    }

    private fun printStat(name: String, value: Int?) {
        if (value == 0) {
            log.warn("{}: {}", name.padEnd(20), value)
        } else {
            log.info("{}: {}", name.padEnd(20), value)
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
