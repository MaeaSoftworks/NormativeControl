package com.maeasoftworks.docx4nc.model

import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import org.jvnet.jaxb2_commons.ppp.Child

class Resolver(private val resolver: PropertyResolver) {
    private var pPrs: MutableMap<String, PPr> = HashMap()
    private var rPrs: MutableMap<R, RPr> = HashMap()

    fun getEffectivePPr(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getEffectivePPr(p.pPr).also { pPrs[p.paraId] = it }
    }

    fun getEffectiveRPr(r: R): RPr {
        return rPrs[r] ?: resolver.getEffectiveRPr(
            r.rPr,
            pPrs[(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P).paraId]
                ?: getEffectivePPr(if (r.parent is P) r.parent as P else (r.parent as Child).parent as P)
        ).also { rPrs[r] = it }
    }
}
