package com.maeasoftworks.polonium.tweaks

import com.maeasoftworks.polonium.parsers.DocumentParser
import org.docx4j.model.PropertyResolver
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

/**
 * Wrapper for [PropertyResolver] that store all property objects to prevent repeated initialization of properties object.
 *
 * todo: try to add unique id for R in document to cache them
 * @param resolver default resolver from mlPackage
 * @param root parser that stores this wrapper
 */
class PropertiesStorage(private val resolver: PropertyResolver, private val root: DocumentParser) {
    private var pPrs: MutableMap<String, PPr> = HashMap()
    private var rPrs: MutableMap<R, RPr> = HashMap()

    /**
     * Get paragraph's actual properties by `paraId`
     * @param p paragraph whose properties need to find and store
     * @return actual properties of paragraph
     */
    operator fun get(p: P): PPr {
        return pPrs[p.paraId] ?: resolver.getActualPPr(p, root.mlPackage).also { pPrs[p.paraId] = it }
    }

    /**
     * Get run's actual properties by run
     * @param r run whose properties need to find and store
     * @return actual properties of run
     */
    operator fun get(r: R): RPr {
        return rPrs[r] ?: resolver.getActualRPr(r, root.mlPackage).also { rPrs[r] = it }
    }
}