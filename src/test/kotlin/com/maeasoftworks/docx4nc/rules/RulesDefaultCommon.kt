package com.maeasoftworks.docx4nc.rules

import com.maeasoftworks.docx4nc.Rules
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import org.junit.jupiter.api.Test

open class RulesDefaultCommon : RulesTestBase() {
    init {
        parser = createParser("rules/default/common")
    }

    @Test
    fun `bordered failed`() {
        base(2, Rules.Default.Common.P::notBordered) {
            it != null && it.mistakeType == TEXT_COMMON_BORDER
        }
    }

    @Test
    fun `bordered passed`() {
        base(3, Rules.Default.Common.P::notBordered) {
            it == null
        }
    }

    @Test
    fun `not background failed`() {
        base(4, Rules.Default.Common.P::hasNotBackground) {
            it != null && it.mistakeType == TEXT_COMMON_BACKGROUND_FILL
        }
    }

    @Test
    fun `not background passed`() {
        base(5, Rules.Default.Common.P::hasNotBackground) {
            it == null
        }
    }

    @Test
    fun `is times new roman failed`() {
        base(6, Rules.Default.Common.R::isTimesNewRoman) {
            it != null && it.mistakeType == TEXT_COMMON_FONT
        }
    }

    @Test
    fun `is times new roman passed`() {
        base(7, Rules.Default.Common.R::isTimesNewRoman) {
            it == null
        }
    }

    @Test
    fun `font size is 14 failed`() {
        base(8, Rules.Default.Common.R::fontSizeIs14) {
            it != null && it.mistakeType == TEXT_COMMON_INCORRECT_FONT_SIZE
        }
    }

    @Test
    fun `font size is 14 passed`() {
        base(9, Rules.Default.Common.R::fontSizeIs14) {
            it == null
        }
    }

    @Test
    fun `not italic failed`() {
        base(10, Rules.Default.Common.R::notItalic) {
            it != null && it.mistakeType == TEXT_COMMON_ITALIC_TEXT
        }
    }

    @Test
    fun `not italic passed`() {
        base(11, Rules.Default.Common.R::notItalic) {
            it == null
        }
    }

    @Test
    fun `not crossed out failed`() {
        base(12, Rules.Default.Common.R::notCrossedOut) {
            it != null && it.mistakeType == TEXT_COMMON_STRIKETHROUGH
        }
    }

    @Test
    fun `not crossed out passed`() {
        base(13, Rules.Default.Common.R::notCrossedOut) {
            it == null
        }
    }

    @Test
    fun `not highlighted failed`() {
        base(14, Rules.Default.Common.R::notHighlighted) {
            it != null && it.mistakeType == TEXT_COMMON_HIGHLIGHT
        }
    }

    @Test
    fun `not highlighted passed`() {
        base(15, Rules.Default.Common.R::notHighlighted) {
            it == null
        }
    }

    @Test
    fun `is black failed`() {
        base(16, Rules.Default.Common.R::isBlack) {
            it != null && it.mistakeType == TEXT_COMMON_TEXT_COLOR
        }
    }

    @Test
    fun `is black passed`() {
        base(17, Rules.Default.Common.R::isBlack) {
            it == null
        }
    }

    @Test
    fun `letter spacing failed`() {
        base(18, Rules.Default.Common.R::letterSpacingIs0) {
            it != null && it.mistakeType == TEXT_COMMON_RUN_SPACING
        }
    }

    @Test
    fun `letter spacing passed`() {
        base(19, Rules.Default.Common.R::letterSpacingIs0) {
            it == null
        }
    }
}
