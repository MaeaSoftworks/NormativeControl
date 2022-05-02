package com.maeasoftworks.normativecontrol.components

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class HeadersKeywords {
    val body: List<String>? = null
    val allKeywords: MutableList<String> = ArrayList()
    val keywordsBySector: MutableList<MutableList<String>> = ArrayList()

    @Value("#{'\${document.sectors.keywords.annotation}'.split(',')}")
    lateinit var annotation: MutableList<String>

    @Value("#{'\${document.sectors.keywords.contents}'.split(',')}")
    lateinit var contents: MutableList<String>

    @Value("#{'\${document.sectors.keywords.introduction}'.split(',')}")
    lateinit var introduction: MutableList<String>

    @Value("#{'\${document.sectors.keywords.conclusion}'.split(',')}")
    lateinit var conclusion: MutableList<String>

    @Value("#{'\${document.sectors.keywords.references}'.split(',')}")
    lateinit var references: MutableList<String>

    @Value("#{'\${document.sectors.keywords.appendix}'.split(',')}")
    lateinit var appendix: MutableList<String>

    final var maxLength = -1
        get() {
            if (field == -1) {
                field = findLongest(contents)
                    .coerceAtLeast(findLongest(introduction))
                    .coerceAtLeast(
                        findLongest(annotation)
                            .coerceAtLeast(findLongest(conclusion))
                    )
                    .coerceAtLeast(
                        findLongest(references)
                            .coerceAtLeast(findLongest(appendix))
                    )
            }
            return field
        }
        private set

    @PostConstruct
    fun init() {
        // front page skip
        allKeywords.addAll(annotation)
        allKeywords.addAll(contents)
        allKeywords.addAll(introduction)
        //allKeywords.addAll(body) // body skip
        allKeywords.addAll(conclusion)
        allKeywords.addAll(references)
        allKeywords.addAll(appendix)
        keywordsBySector.add(ArrayList()) // front page
        keywordsBySector.add(annotation)
        keywordsBySector.add(contents)
        keywordsBySector.add(introduction)
        keywordsBySector.add(ArrayList()) // body
        keywordsBySector.add(conclusion)
        keywordsBySector.add(references)
        keywordsBySector.add(appendix)
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