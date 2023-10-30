package ru.maeasoftworks.normativecontrol.api.students.components

import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class Launcher(
    private val s3Storage: S3Storage
) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    fun run(documentId: String, file: Flux<DataBuffer>, accessKey: String): Flux<Message> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<Message>()
        executor.execute(Runner(documentId, file, accessKey, s3Storage, ParserCallback(documentId, MessageSender(sink))))
        return sink.asFlux()
    }
}
