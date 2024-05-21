package normativecontrol.implementation.urfu

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import normativecontrol.core.Core
import normativecontrol.core.Runtime
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.locales.Locales
import normativecontrol.implementation.urfu.handlers.PHandler
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
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
                    "1 Abc." to Chapters.Body
                )
            ) {
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
                    val pHandler = runtime.handlers[PHandler::class] as PHandler
                    val textHolder = PHandler::class.memberProperties.find { it.name == "text" }?.get(pHandler)
                    val value = textHolder!!::class.memberProperties.find { it.name == "value" } as KMutableProperty<*>
                    value.isAccessible = true
                    pHandler.chapterHeaderHandler.checkChapterStart(Text().apply { this.value = it.first }) shouldBe it.second
                    value.setter.call(textHolder, null)
                }
            }
        }
    }
})