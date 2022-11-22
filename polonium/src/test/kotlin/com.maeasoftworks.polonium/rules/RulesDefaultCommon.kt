package com.maeasoftworks.polonium.rules

import com.maeasoftworks.polonium.enums.MistakeType.*
import com.maeasoftworks.polonium.model.Rules
import org.junit.jupiter.api.Test

open class RulesDefaultCommon : RulesTestHelper() {
    init {
        parser = createParser("rules/default/common")
    }

    @Test
    fun `bordered failed`() {
        testRule(2, Rules.Default.Common.P.notBordered, TEXT_COMMON_BORDER)
    }

    @Test
    fun `bordered passed`() {
        testRule(3, Rules.Default.Common.P.notBordered)
    }

    @Test
    fun `not background failed`() {
        testRule(4, Rules.Default.Common.P.hasNotBackground, TEXT_COMMON_BACKGROUND_FILL)
    }

    @Test
    fun `not background passed`() {
        testRule(5, Rules.Default.Common.P.hasNotBackground)
    }

    @Test
    fun `is times new roman failed`() {
        testRule(6, Rules.Default.Common.R.isTimesNewRoman, TEXT_COMMON_FONT)
    }

    @Test
    fun `is times new roman passed`() {
        testRule(7, Rules.Default.Common.R.isTimesNewRoman)
    }

    @Test
    fun `font size is 14 failed`() {
        testRule(8, Rules.Default.Common.R.fontSizeIs14, TEXT_COMMON_INCORRECT_FONT_SIZE)
    }

    @Test
    fun `font size is 14 passed`() {
        testRule(9, Rules.Default.Common.R.fontSizeIs14)
    }

    @Test
    fun `not italic failed`() {
        testRule(10, Rules.Default.Common.R.notItalic, TEXT_COMMON_ITALIC_TEXT)
    }

    @Test
    fun `not italic passed`() {
        testRule(11, Rules.Default.Common.R.notItalic)
    }

    @Test
    fun `not crossed out failed`() {
        testRule(12, Rules.Default.Common.R.notCrossedOut, TEXT_COMMON_STRIKETHROUGH)
    }

    @Test
    fun `not crossed out passed`() {
        testRule(13, Rules.Default.Common.R.notCrossedOut)
    }

    @Test
    fun `not highlighted failed`() {
        testRule(14, Rules.Default.Common.R.notHighlighted, TEXT_COMMON_HIGHLIGHT)
    }

    @Test
    fun `not highlighted passed`() {
        testRule(15, Rules.Default.Common.R.notHighlighted)
    }

    @Test
    fun `is black failed`() {
        testRule(16, Rules.Default.Common.R.isBlack, TEXT_COMMON_TEXT_COLOR)
    }

    @Test
    fun `is black passed`() {
        testRule(17, Rules.Default.Common.R.isBlack)
    }

    @Test
    fun `letter spacing failed`() {
        testRule(18, Rules.Default.Common.R.letterSpacingIs0, TEXT_COMMON_RUN_SPACING)
    }

    @Test
    fun `letter spacing passed`() {
        testRule(19, Rules.Default.Common.R.letterSpacingIs0)
    }
}
