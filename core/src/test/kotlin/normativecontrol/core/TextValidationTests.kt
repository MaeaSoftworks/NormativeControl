package normativecontrol.core

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.shouldBe
import normativecontrol.core.implementations.ufru.handlers.TextHandler

class TextValidationTests : ShouldSpec({
    context("references") {
        val text = """
        Разрешено использовать только внутритекстовые ссылки на литературу.
        Эти ссылки должны быть размещены непосредственно в строке после текста, к
        которому относятся, перед точкой (окончанием предложения) и оформлены в
        скобках с указанием номера в списке литературы, например, [31]. Ссылки на
        несколько конкретных работ автора могут быть даны, например, в форме [12-17, 19].
        При цитировании, а также в случаях, требующих указания конкретной
        страницы источника, в скобках дополнительно указывается страница [12, С. 7]
        или страницы «от-до» [19, с. 7-9]. Нумерация ссылок должна быть сквозной,
        идущей через всю пояснительную записку. Также распознаваемые вариации: [14, 17 - 21], [15 - 16, 17 - 18]
        """.trimIndent()

        should("brackets found correctly") {
            TextHandler.findAllInBrackets(text) shouldHaveCount 6
        }

        should("find ranges correctly") {
            TextHandler.apply {
                val (refs, ranges) = findAllRanges(clearPages(findAllInBrackets(text)))
                refs shouldHaveCount 6
                ranges.size shouldBe 4
            }
        }

        should("find single references correctly") {
            TextHandler.apply {
                val (refStrings, _) = findAllRanges(clearPages(findAllInBrackets(text)))
                val refs = findAllReferences(refStrings.toList())
                refs shouldHaveSize 5
            }
        }

        should("find all referenced references") {
            val refs = TextHandler.getAllReferences(text)
            /*
            [31]
            [12-17, 19]
            [12, С. 7]
            [19, с. 7-9]
            [14, 17 - 21]
            [15 - 16, 17 - 18]
            */
            refs shouldBe setOf(31, 12, 13, 14, 15, 16, 17, 19, 18, 20, 21)
        }
    }
})