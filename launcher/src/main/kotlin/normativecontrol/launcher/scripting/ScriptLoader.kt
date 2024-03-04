package normativecontrol.launcher.scripting

import normativecontrol.externalapi.Script
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.script.experimental.api.EvaluationResult
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object ScriptLoader {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun loadFile(path: String): ResultWithDiagnostics<EvaluationResult> {
        logger.info("Executing script file: '$path'")
        return loadScript(File(path).readText())
    }

    fun loadScript(script: String): ResultWithDiagnostics<EvaluationResult> {
        val result = BasicJvmScriptingHost().eval(
            script.toScriptSource(),
            createJvmCompilationConfigurationFromTemplate<Script>(),
            null
        )
        return result
    }
}