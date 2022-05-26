package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.model.Chapter

class ContentsParser(chapter: Chapter, root: DocumentParser) : SimpleParser(chapter, root) {
    override fun handleContents(p: Int) {}
}