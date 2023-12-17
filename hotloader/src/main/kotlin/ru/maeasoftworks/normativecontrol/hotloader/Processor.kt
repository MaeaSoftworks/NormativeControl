package ru.maeasoftworks.normativecontrol.hotloader

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class Processor(private val fileGenerator: FileGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation(HotLoaded::class.qualifiedName!!).toList()
        if (annotated.isEmpty()) {
            return emptyList()
        }
        fileGenerator.generate(annotated)
        return emptyList()
    }
}