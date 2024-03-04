package normativecontrol.externalapi

import kotlinx.coroutines.runBlocking
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.CompoundDependenciesResolver
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.FileSystemDependenciesResolver
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.resolveFromScriptSourceAnnotations
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object ScriptConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromCurrentContext("external-api")
    }
    refineConfiguration {
        onAnnotations<DependsOn> { context ->
            val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
                ?: return@onAnnotations context.compilationConfiguration.asSuccess()
            return@onAnnotations runBlocking {
                CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver()).resolveFromScriptSourceAnnotations(annotations)
            }.onSuccess {
                context.compilationConfiguration.with {
                    dependencies.append(JvmDependency(it))
                }.asSuccess()
            }
        }
    }
}) {
    private fun readResolve(): Any = ScriptConfiguration
}