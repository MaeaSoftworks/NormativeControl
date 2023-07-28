package com.maeasoftworks.bootstrap

import com.maeasoftworks.bootstrap.model.ParserCallback
import com.maeasoftworks.bootstrap.model.Runner
import io.minio.MinioClient
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class Bootstrapper(private val minioClient: MinioClient, private val messageSender: MessageSender) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    var counter: AtomicInteger = AtomicInteger(0)

    fun run(documentId: String) {
        executor.execute(Runner(documentId, counter, minioClient, ParserCallback(documentId, messageSender)))
        counter.incrementAndGet()
    }
}
