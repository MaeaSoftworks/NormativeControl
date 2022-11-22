package com.maeasoftworks.polonium.rules

import com.maeasoftworks.polonium.enums.MistakeType.*
import com.maeasoftworks.polonium.model.Rules
import org.junit.jupiter.api.Test

class RulesDefaultHeader : RulesTestHelper() {
    init {
        parser = createParser("rules/default/header")
    }

    @Test
    fun `justify is center failed`() {
        testRule(0, Rules.Default.Header.P.justifyIsCenter, TEXT_HEADER_ALIGNMENT)
    }

    @Test
    fun `justify is center passed`() {
        testRule(1, Rules.Default.Header.P.justifyIsCenter)
    }

    @Test
    fun `line spacing is one failed`() {
        testRule(2, Rules.Default.Header.P.lineSpacingIsOne, TEXT_HEADER_LINE_SPACING)
    }

    @Test
    fun `line spacing is one passed`() {
        testRule(3, Rules.Default.Header.P.lineSpacingIsOne)
    }

    @Test
    fun `has not dot in end failed`() {
        testRule(4, Rules.Default.Header.P.hasNotDotInEnd, TEXT_HEADER_REDUNDANT_DOT)
    }

    @Test
    fun `has not dot in end passed`() {
        testRule(5, Rules.Default.Header.P.hasNotDotInEnd)
    }

    @Test
    fun `empty line after header exists failed`() {
        testRule(6, Rules.Default.Header.P.emptyLineAfterHeaderExists, TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED)
    }

    @Test
    fun `empty line after header exists passed`() {
        testRule(7, Rules.Default.Header.P.emptyLineAfterHeaderExists)
    }

    @Test
    fun `header wrapping failed`() {
        testRule(10, Rules.Default.Header.P.isAutoHyphenSuppressed, TEXT_HEADER_AUTO_HYPHEN)
    }

    @Test
    fun `header wrapping passed`() {
        testRule(11, Rules.Default.Header.P.isAutoHyphenSuppressed)
    }
}
