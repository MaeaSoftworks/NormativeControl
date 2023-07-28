package com.maeasoftworks.core.parsers

import com.maeasoftworks.core.model.Chapter

class ContentsParser(chapter: Chapter, root: DocumentParser) : SimpleParser(chapter, root) {
    override fun handleContents(p: Int) {}
}
