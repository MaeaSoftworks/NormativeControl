package com.maeasoftworks.docx4nc.parser

import org.junit.jupiter.api.Test

class TableTests : ParserTestFactory(TableTests::class) {
    @Test
    fun `table found properly`() {
        val parser = createParser("tableFinder.docx")
        parser.findChapters()
    }
}