package normativecontrol.implementation.urfu

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import normativecontrol.core.Core
import normativecontrol.core.Runtime
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.locales.Locales
import normativecontrol.implementation.urfu.handlers.PHandler
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

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
        или страницы «от-до» [19, С. 7-9]. Нумерация ссылок должна быть сквозной,
        идущей через всю пояснительную записку. Также распознаваемые вариации: [14, 17 - 21], [50 - 56, 60 - 66], [100, С. 200-202, 101, С. 203-204]
        """.trimIndent()

        should("find all referenced references") {
            /*
            [31]
            [12-17, 19]
            [12, С. 7]
            [19, С. 7-9]
            [14, 17 - 21]
            [50 - 56, 60 - 66]
            */
            Core
            val runtime = Runtime(
                UrFUConfiguration.NAME,
                mapOf(UrFUConfiguration.NAME to { UrFUConfiguration() })
            )
            val ctx = VerificationContext(
                runtime,
                WordprocessingMLPackage.createPackage(),
                Locales.RU
            )
            runtime.context = ctx

            with(ctx) {
                val handler = runtime.handlers[PHandler::class] as PHandler
                val textHolder = PHandler::class.memberProperties.find { it.name == "text" }?.apply { isAccessible = true }?.get(handler)
                val fn = textHolder!!::class.functions.find { it.name == "getAllReferences" }
                fn?.isAccessible = true
                fn?.call(textHolder, text) shouldBe setOf(31, 12, 13, 14, 15, 16, 17, 19, 18, 20, 21, 50, 51, 52, 53, 54, 55, 56, 60, 61, 62, 63, 64, 65, 66, 100)
            }
        }
    }
})