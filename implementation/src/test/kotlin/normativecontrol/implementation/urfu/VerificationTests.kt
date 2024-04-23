package normativecontrol.implementation.urfu

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.implementation.urfu.handlers.PHandler
import org.docx4j.wml.Text
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class VerificationTests : ShouldSpec({
    context(PHandler::class.simpleName!!) {
        context("body header tests") {
            withData(
                { it.first },
                ts = sequenceOf(
                    "1.2 Abc" to Chapters.Body,
                    "1.2. Abc" to null,
                    "1.2Abc" to null,
                    "1. Abc" to null,
                    "1 Abc" to Chapters.Body,
                    "1              Abc" to null,
                    "1" to null,
                    "1             " to null,
                    "1 Abc abc abc abc" to Chapters.Body,
                    "1 Abc." to null
                )
            ) {
                with(VerificationContext(UrFUConfiguration())) {
                    val pHandler = PHandler()
                    val currentText = pHandler::class.memberProperties.find { it.name == pHandler::currentText.name } as KMutableProperty<*>
                    currentText.isAccessible = true
                    pHandler.checkChapterStart(Text().apply { value = it.first }) shouldBe it.second
                    currentText.setter.call(pHandler, null)
                }
            }
        }
    }
})