package me.prmncr.hotloader

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class HotLoaderProcessor(private val hotLoaderGenerator: HotLoaderGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation(HotLoaded::class.qualifiedName!!).toList()
        if (annotated.isEmpty()) {
            return emptyList()
        }
        hotLoaderGenerator.generate(annotated)
        return emptyList()
    }
}