package com.prmncr.normativecontrol.components

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class SectorKeywords {
    var allKeywordsFlat: MutableList<String> = ArrayList()
    var allKeywords: MutableList<List<String>> = ArrayList()

    @Value("#{'\${document.sectors.keywords.annotation}'.split(',')}")
    private val annotation: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.contents}'.split(',')}")
    private val contents: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.introduction}'.split(',')}")
    private val introduction: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.body}'.split(',')}")
    private val body: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.conclusion}'.split(',')}")
    private val conclusion: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.references}'.split(',')}")
    private val references: MutableList<String>? = null

    @Value("#{'\${document.sectors.keywords.appendix}'.split(',')}")
    private val appendix: MutableList<String>? = null
    var maxLength = -1
        get() {
            if (field == -1) {
                field = findLongest(contents).coerceAtLeast(findLongest(introduction))
                    .coerceAtLeast(findLongest(annotation).coerceAtLeast(findLongest(conclusion)))
                    .coerceAtLeast(findLongest(references).coerceAtLeast(findLongest(appendix)))
            }
            return field
        }

    @PostConstruct
    fun init() {
        allKeywordsFlat.addAll(annotation!!)
        allKeywordsFlat.addAll(contents!!)
        allKeywordsFlat.addAll(introduction!!)
        allKeywordsFlat.addAll(body!!)
        allKeywordsFlat.addAll(conclusion!!)
        allKeywordsFlat.addAll(references!!)
        allKeywordsFlat.addAll(appendix!!)
        allKeywords.add(annotation)
        allKeywords.add(contents)
        allKeywords.add(introduction)
        allKeywords.add(body)
        allKeywords.add(conclusion)
        allKeywords.add(references)
        allKeywords.add(appendix)
    }

    private fun findLongest(keywords: List<String>?): Int {
        var max = -1
        for (keyword in keywords!!) {
            val len = keyword.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
            if (len > max) {
                max = len
            }
        }
        return max
    }
}