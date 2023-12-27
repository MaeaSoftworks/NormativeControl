package me.prmncr.hotloader

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class HotLoaderGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(annotated: List<KSAnnotated>, basePackage: String?) {
        if (annotated.isEmpty()) {
            return
        }

        val pkg = basePackage ?: this::class.qualifiedName!!.split(".").dropLast(1).joinToString(".")
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
                    .addProperty(
                        PropertySpec.builder("logger", Logger::class, listOf(KModifier.PRIVATE))
                            .initializer(CodeBlock.builder().addStatement("%T.getLogger(this::class.java)", LoggerFactory::class).build())
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("load")
                            .addCode(
                                """
                                if (loaded) return
                                unsafeLoad()
                                loaded = true
                            """.trimIndent()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("unsafeLoad")
                            .addModifiers(KModifier.PRIVATE)
                            .addCode("val classes = listOf(\n")
                            .apply {
                                annotated.forEach {
                                    addCode((it as KSDeclaration).qualifiedName!!.let { n -> "${n.asString()},\n" })
                                }
                            }
                            .addCode(")\n")
                            .addStatement("logger.debug(\"Loaded classes: [\${ classes.map { it::class.simpleName }.joinToString() }]\")")
                            .build()
                    ).build()
            ).build()
        val stream = try {
            codeGenerator.createNewFile(Dependencies(aggregating = true, *annotated.mapNotNull { it.containingFile }.toTypedArray()), pkg, name)
        } catch (e: FileAlreadyExistsException) {
            e.file.outputStream()
        }
        OutputStreamWriter(stream, StandardCharsets.UTF_8).use(code::writeTo)
    }
}