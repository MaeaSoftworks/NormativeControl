package com.prmncr.normativecontrol.dtos

data class Error(val chapterId: Int, val paragraphId: Int, val runId: Int, val errorType: ErrorType?) {
    // to page
    constructor(errorType: ErrorType?) : this(-1, -1, -1, errorType)

    // unknown chapter
    constructor(paragraph: Int, run: Int, errorType: ErrorType?) : this(-1, paragraph, run, errorType)

    // to all chapter
    constructor(chapter: Long, errorType: ErrorType?) : this(chapter.toInt(), -1, -1, errorType)

    // to all paragraphId
    constructor(paragraph: Int, errorType: ErrorType?) : this(-1, paragraph, -1, errorType)
}