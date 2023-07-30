package com.maeasoftworks.core.model

/**
 * Storage for keywords which are used to detect chapters type
 *
 * todo: try to make markers more independent of keywords or remove hardcoded keywords
 */
object ChapterMarkers {

    /**
     * All keywords in 2D list
     */
    val markers: List<List<String>>

    /**
     * Keywords for 'Annotation'
     */
    val annotation = listOf("РЕФЕРАТ")

    /**
     * Keywords for 'Contents'
     */
    private val contents = listOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ")

    /**
     * Keywords for 'Introduction'
     */
    private val introduction = listOf("ВВЕДЕНИЕ")

    /**
     * Keywords for 'Conclusion'
     */
    private val conclusion = listOf("ЗАКЛЮЧЕНИЕ")

    /**
     * Keywords for 'References'
     */
    private val references =
        listOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ")

    /**
     * Keywords for 'Appendix'
     */
    val appendix = listOf("ПРИЛОЖЕНИЕ", "ПРИЛОЖЕНИЯ")

    /**
     * Initialization for `markers` list. Filling order:
     * - Front page mock
     * - Annotation
     * - Contents
     * - Introduction
     * - Body mock
     * - Conclusion
     * - References
     * - Appendix
     */
    init {
        markers = listOf(
            ArrayList(), // front page
            annotation,
            contents,
            introduction,
            ArrayList(), // body
            conclusion,
            references,
            appendix
        )
    }
}
