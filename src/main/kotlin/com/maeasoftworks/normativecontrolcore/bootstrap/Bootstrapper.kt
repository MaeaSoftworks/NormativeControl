package com.maeasoftworks.normativecontrolcore.bootstrap

import com.maeasoftworks.normativecontrolcore.bootstrap.adapters.S3Adapter
import com.maeasoftworks.normativecontrolcore.bootstrap.model.ParserCallback
import com.maeasoftworks.normativecontrolcore.bootstrap.model.Runner
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class Bootstrapper(
    private val s3Adapter: S3Adapter,
    private val messageSender: MessageSender) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    var counter: AtomicInteger = AtomicInteger(0)

    fun run(documentId: String) {
        executor.execute(Runner(documentId, counter, s3Adapter, ParserCallback(documentId, messageSender)))
        counter.incrementAndGet()
    }
}
