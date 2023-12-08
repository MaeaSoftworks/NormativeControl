package ru.maeasoftworks.normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.PVerifier
import ru.maeasoftworks.normativecontrol.core.invoke

class VerificationTests: ShouldSpec({
    context(PVerifier::class.simpleName!!) {
        should("regex works properly") {
            val fn = PVerifier { "isChapterBodyHeader" }
            fn("1.2 Abc") shouldBe true
            fn("1.2. Abc") shouldBe false
            fn("1.2Abc") shouldBe false
            fn("1. Abc") shouldBe false
            fn("1 Abc") shouldBe true
            fn("1              Abc") shouldBe false
            fn("1") shouldBe false
            fn("1             ") shouldBe false
            fn("1 Abc abc abc abc") shouldBe true
            fn("1 Abc.") shouldBe false
        }

        should("chapter order detects properly") {

        }
    }
})