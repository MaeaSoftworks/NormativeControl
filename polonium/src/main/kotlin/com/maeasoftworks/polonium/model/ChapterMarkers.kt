package com.maeasoftworks.polonium.model

/**
 * Storage for keywords which are used to detect chapters type
 * @author prmncr
 */
object ChapterMarkers {

    /**
     * All keywords in 2D list
     * @author prmncr
     */
    val markers: List<List<String>>

    /**
     * Keywords for 'Annotation'
     * @author prmncr
     */
    val annotation = listOf("РЕФЕРАТ")

    /**
     * Keywords for 'Contents'
     * @author prmncr
     */
    val contents = listOf("СОДЕРЖАНИЕ", "ОГЛАВЛЕНИЕ")

    /**
     * Keywords for 'Introduction'
     * @author prmncr
     */
    val introduction = listOf("ВВЕДЕНИЕ")

    /**
     * Keywords for 'Conclusion'
     * @author prmncr
     */
    val conclusion = listOf("ЗАКЛЮЧЕНИЕ")

    /**
     * Keywords for 'References'
     * @author prmncr
     */
    val references =
        listOf("БИБЛИОГРАФИЧЕСКИЙ СПИСОК", "СПИСОК ИСПОЛЬЗОВАННОЙ ЛИТЕРАТУРЫ", "СПИСОК ЛИТЕРАТУРЫ", "СПИСОК ИСТОЧНИКОВ")

    /**
     * Keywords for 'Appendix'
     * @author prmncr
     */
    val appendix = listOf("ПРИЛОЖЕНИЕ", "ПРИЛОЖЕНИЯ")

    /**
     * Initialization for <code>markers</code> list. Filling order:
     * - Front page mock
     * - Annotation
     * - Contents
     * - Introduction
     * - Body mock
     * - Conclusion
     * - References
     * - Appendix
     * @author prmncr
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
            appendix,
        )
    }
}
