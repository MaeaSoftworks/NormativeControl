package normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterConfiguration

class ChaptersTests: ShouldSpec({
    should("create config with correct order") {
        val config = ChapterConfiguration.create<TestChapters1>()
        config.getPrependChapters(TestChapters1.A) shouldBe emptyArray()
        config.getPrependChapters(TestChapters1.B) shouldBe arrayOf(TestChapters1.A)
        config.getPrependChapters(TestChapters1.C) shouldBe arrayOf(TestChapters1.A, TestChapters1.B)
        config.getPrependChapters(TestChapters1.D) shouldBe arrayOf(TestChapters1.C, TestChapters1.D)
    }
}) {
    enum class TestChapters1(override val names: Array<String>, override val canBeAfterChapters: (() -> Array<Chapter>)? = null): Chapter {
        A(arrayOf("a")),                    //  a -> b -> c -> d -> d -> d...
        B(arrayOf("b"), { arrayOf(A) }),        //  or
        C(arrayOf("c"), { arrayOf(A, B) }),     //  a -> c -> d -> d -> d...
        D(arrayOf("d"), { arrayOf(C, D) })      //
    }
}