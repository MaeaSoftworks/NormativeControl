package normativecontrol.launcher

import normativecontrol.core.Core
import normativecontrol.core.locales.Locales
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.launcher.client.components.JobPool
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

@Command(name = "verify", description = ["Verify document and exit."], mixinStandardHelpOptions = true)
class Verifier : Runnable {
    @Parameters(index = "0", description = ["File that need to be verified."])
    private lateinit var source: String

    @Parameters(index = "1", defaultValue = Option.NULL_VALUE, description = ["Path to result file. If not specified, it will be saved in same folder as source file."])
    private var result: String? = null

    @Parameters(
        index = "2",
        defaultValue = Option.NULL_VALUE,
        description = ["Path to rendered document in HTML file. File will be opened after verification. If not specified, file will not be rendered."]
    )
    private var render: String? = null

    @Option(names = ["-b"], description = ["Enable blocking mode (instead of multithreading)."])
    private var isBlocking = false

    private val parallelMode: ParallelMode
        get() = if (isBlocking) ParallelMode.SINGLE else ParallelMode.THREADS

    override fun run() {
        JobPool.initialize(parallelMode)
        JobPool.run(runnable = {
            val file = File(source)
            val verificationResult = Core.verify(file.inputStream(), UrFUConfiguration.NAME, Locales.RU)
            verificationResult.docx.writeTo(FileOutputStream(result ?: (file.parent + File.separator + "result.docx")))
            if (render != null) {
                Files.createFile(Path.of(render!!)).toFile().also {
                    it.writeText(verificationResult.html)
                }
            }
        })
    }
}