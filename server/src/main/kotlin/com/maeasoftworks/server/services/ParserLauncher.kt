package com.maeasoftworks.server.services

import com.maeasoftworks.server.components.Runner
import com.maeasoftworks.server.dao.ParserCallback
import com.maeasoftworks.server.senders.ResultSender
import io.minio.MinioClient
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class ParserLauncher(private val minioClient: MinioClient, private val resultSender: ResultSender) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    var counter: AtomicInteger = AtomicInteger(0)

    fun run(documentId: String) {
        executor.execute(Runner(documentId, counter, minioClient, ParserCallback(documentId, resultSender)))
        counter.incrementAndGet()
    }
}
