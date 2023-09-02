package bootstrapper

import bootstrapper.adapters.S3Adapter
import bootstrapper.model.ParserCallback
import bootstrapper.model.Runner
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class Launcher(
    private val s3Adapter: S3Adapter,
    private val messageSender: MessageSender
) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    fun run(documentId: String) {
        executor.execute(
            Runner(documentId, s3Adapter, ParserCallback(documentId, messageSender))
        )
    }
}
