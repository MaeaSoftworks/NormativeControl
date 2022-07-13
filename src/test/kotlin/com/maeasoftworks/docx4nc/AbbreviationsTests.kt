package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.enums.MistakeType
import org.docx4j.wml.P
import org.junit.jupiter.api.Test

class AbbreviationsTests: ParserTestFactory(AbbreviationsTests::class) {
    @Test
    fun `abbreviation found`() {
        val parser = createParser("test.docx")
        parser.texts.getText(parser.doc.content[0] as P)
        errorAssert(parser.mistakes, MistakeType.TEXT_ABANDONED_ABBREVIATION_FOUND)
    }
}