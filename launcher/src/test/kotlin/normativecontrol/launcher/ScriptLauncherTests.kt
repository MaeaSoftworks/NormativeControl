package normativecontrol.launcher

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import normativecontrol.externalapi.Script
import normativecontrol.launcher.scripting.ScriptLoader
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

class ScriptLauncherTests: ShouldSpec({
    should("launch") {
        val result = ScriptLoader.loadFile("scripts/script1.nc.kts")
        result.shouldBeInstanceOf<ResultWithDiagnostics.Success<*>>()
    }
})