package me.prmncr.hotloader

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class HotLoaderProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HotLoaderProcessor(HotLoaderGenerator(codeGenerator = environment.codeGenerator), environment.options)
    }
}