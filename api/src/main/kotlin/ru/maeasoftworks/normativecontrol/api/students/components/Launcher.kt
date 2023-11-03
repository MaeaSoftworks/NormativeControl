package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Service
class Launcher(
    private val s3Storage: S3Storage
) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()

    fun run(documentId: String, file: Flux<DataBuffer>, accessKey: String): Flow<Message> {
        val channel = Channel<Message>(-1)
        executor.execute(Runner(documentId, file, accessKey, s3Storage, ParserCallback(documentId, MessageSender(channel))))
        return channel.consumeAsFlow()
    }
}
