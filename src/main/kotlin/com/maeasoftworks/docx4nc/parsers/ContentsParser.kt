package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

class ContentsParser(chapter: Chapter, root: DocumentParser) : SimpleParser(chapter, root) {
    override fun handleContents(p: Int) {}
}
