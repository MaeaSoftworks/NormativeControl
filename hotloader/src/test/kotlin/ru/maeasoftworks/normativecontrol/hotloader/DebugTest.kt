package ru.maeasoftworks.normativecontrol.hotloader

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.test.assertEquals

class DebugTest {
    @Test
    fun generate() {
        val result = KotlinCompilation().apply {
            val files = mutableListOf<SourceFile>()

            Files.walkFileTree(Path("../core/src/main"), object : FileVisitor<Path> {
                override fun preVisitDirectory(p0: Path?, p1: BasicFileAttributes?): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }

                override fun visitFile(p0: Path?, p1: BasicFileAttributes?): FileVisitResult {
                    files += SourceFile.fromPath(File(p0.toString()))
                    return FileVisitResult.CONTINUE
                }

                override fun visitFileFailed(p0: Path?, p1: IOException?): FileVisitResult {
                    return FileVisitResult.TERMINATE
                }

                override fun postVisitDirectory(p0: Path?, p1: IOException?): FileVisitResult {
                    return FileVisitResult.CONTINUE
                }
            })
            sources = files
            inheritClassPath = true
            languageVersion = "1.9"
            symbolProcessorProviders = listOf(ProcessorProvider())
        }.compile()
        println(result.outputDirectory)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}