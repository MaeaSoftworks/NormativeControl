package ru.maeasoftworks.normativecontrol.hotloader

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.*
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class FileGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(annotated: List<KSAnnotated>) {
        val pkg = "ru.maeasoftworks.normativecontrol.hotloader"
        val name = "HotLoader"

        val code = FileSpec.builder(ClassName(pkg, name))
            .addType(
                TypeSpec.objectBuilder("HotLoader")
                    .addProperty(
                        PropertySpec.builder("loaded", Boolean::class, listOf(KModifier.PRIVATE))
                            .initializer(CodeBlock.of("false"))
                            .mutable()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("safeLoad")
                            .addCode(
                                """
                                if (loaded) return
                                load()
                                loaded = true
                            """.trimIndent()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("load")
                            .addModifiers(KModifier.PRIVATE)
                            .addCode("val classes = listOf(\n")
                            .apply {
                                annotated.forEach {
                                    addCode((it as KSDeclaration).qualifiedName!!.let { n -> "${n.asString()},\n" })
                                }
                            }
                            .addCode(")\n")
                            .addStatement("classes.forEach { it.apply { println(\"[HotLoader] \${this::class.simpleName} was loaded\") } }")
                            .build()
                    ).build()
            ).build()
        val stream = try {
            codeGenerator.createNewFile(
                Dependencies(aggregating = true, *annotated.map { it.containingFile!! }.toTypedArray()),
                pkg,
                name
            )
        } catch (e: FileAlreadyExistsException) {
            e.file.outputStream()
        }
        OutputStreamWriter(stream, StandardCharsets.UTF_8).use(code::writeTo)
    }
}