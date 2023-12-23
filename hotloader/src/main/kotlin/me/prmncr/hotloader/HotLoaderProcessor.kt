package me.prmncr.hotloader

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class HotLoaderProcessor(private val hotLoaderGenerator: HotLoaderGenerator, private val args: Map<String, String>) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotated = resolver.getSymbolsWithAnnotation(HotLoaded::class.qualifiedName!!).toList()
        if (annotated.isEmpty()) {
            return emptyList()
        }
        val basePackage = args["hotloader.basePackage"]
        hotLoaderGenerator.generate(annotated, basePackage)
        return emptyList()
    }
}