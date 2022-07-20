package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.model.Chapter

class ContentsParser(chapter: Chapter, root: DocumentParser) : SimpleParser(chapter, root) {
    override fun handleContents(p: Int) {}
}
