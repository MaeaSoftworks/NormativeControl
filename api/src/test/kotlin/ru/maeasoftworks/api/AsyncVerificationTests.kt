package ru.maeasoftworks.api

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.KeyGenerator
import ru.maeasoftworks.normativecontrol.api.infrastructure.verification.VerificationService
import java.io.ByteArrayInputStream
import java.io.File

class AsyncVerificationTests : ShouldSpec({
    val file = File("src/test/resources/ignore/sample1.docx").readBytes()

    context("test application with websocket controller") {
        suspend fun verification(id: Int, file: ByteArray) {
            val start = System.nanoTime()
            val fileCopy = ByteArray(file.size) // emulation of websocket's ByteArray creation
            for ((pos, i) in file.indices.withIndex()) {
                fileCopy[pos] = file[i]
            }
            VerificationService.startVerification(KeyGenerator.generate(10), KeyGenerator.generate(10), ByteArrayInputStream(fileCopy))
            val end = System.nanoTime()
            println("Runner $id: ${end - start} nanoseconds")
        }

        should("works fine on 1 binary frame") {
            verification(0, file)
        }

        should("works async") {
            verification(0, file)
            val startMany = System.nanoTime()
            val list = List(100) { async(start = CoroutineStart.LAZY) { verification(it, file) } }
            awaitAll(*list.toTypedArray())
            val endMany = System.nanoTime()
            println("100 runs: ${endMany - startMany} nanoseconds")
        }
    }
})