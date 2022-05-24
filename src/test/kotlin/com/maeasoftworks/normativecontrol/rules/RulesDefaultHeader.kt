package com.maeasoftworks.normativecontrol.rules

import com.maeasoftworks.normativecontrol.parser.chapters.Rules
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.junit.jupiter.api.Test

class RulesDefaultHeader: RulesTestBase() {
    init {
        parser = createParser("rules/default/header")
    }

    @Test
    fun `justify is center failed`() {
        base(0, PFunctionWrapper(Rules.Default.Header.P::justifyIsCenter)) {
            it != null && it.errorType == ErrorType.TEXT_HEADER_ALIGNMENT
        }
    }

    @Test
    fun `justify is center passed`() {
        base(1, PFunctionWrapper(Rules.Default.Header.P::justifyIsCenter)) {
            it == null
        }
    }
    @Test
    fun `line spacing is one failed`() {
        base(2, PFunctionWrapper(Rules.Default.Header.P::lineSpacingIsOne)) {
            it != null && it.errorType == ErrorType.TEXT_HEADER_LINE_SPACING
        }
    }

    @Test
    fun `line spacing is one passed`() {
        base(3, PFunctionWrapper(Rules.Default.Header.P::lineSpacingIsOne)) {
            it == null
        }
    }

    @Test
    fun `has not dot in end failed`() {
        base(4, PFunctionWrapper(Rules.Default.Header.P::hasNotDotInEnd)) {
            it != null && it.errorType == ErrorType.TEXT_HEADER_REDUNDANT_DOT
        }
    }

    @Test
    fun `has not dot in end passed`() {
        base(5, PFunctionWrapper(Rules.Default.Header.P::hasNotDotInEnd)) {
            it == null
        }
    }

    @Test
    fun `empty line after header exists failed`() {
        base(6, PFunctionWrapper(Rules.Default.Header.P::emptyLineAfterHeaderExists)) {
            it != null && it.errorType == ErrorType.TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED
        }
    }

    @Test
    fun `empty line after header exists passed`() {
        base(7, PFunctionWrapper(Rules.Default.Header.P::emptyLineAfterHeaderExists)) {
            it == null
        }
    }
}