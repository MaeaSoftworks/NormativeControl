package com.maeasoftworks.docx4nc.tweaks

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

class PropertiesStorage(private val resolver: PropertyResolver, private val root: DocumentParser) {
    private var pPrs: MutableMap<String, PPr> = HashMap()
    private var rPrs: MutableMap<R, RPr> = HashMap()

    operator fun get(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getEffectivePPr(p, root.mlPackage).also { pPrs[p.paraId] = it }
    }

    operator fun get(r: R): RPr {
        return rPrs[r] ?: resolver.getEffectiveRPr(r, root.mlPackage).also { rPrs[r] = it }
    }
}