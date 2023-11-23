package ru.maeasoftworks.normativecontrol.model

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.maeasoftworks.normativecontrol.core.model.Context
import ru.maeasoftworks.normativecontrol.core.parsers.DocumentParser
import ru.maeasoftworks.normativecontrol.core.rendering.RenderLauncher
import ru.maeasoftworks.normativecontrol.dto.Message
import ru.maeasoftworks.normativecontrol.utils.Rat
import ru.maeasoftworks.normativecontrol.extensions.uploadConclusion
import ru.maeasoftworks.normativecontrol.extensions.uploadRender
import ru.maeasoftworks.normativecontrol.utils.with
import ru.maeasoftworks.normativecontrol.modules.S3
import java.io.ByteArrayOutputStream
import java.io.InputStream

data class StageHolder(var stage: Message.Stage)

suspend fun startVerification(documentId: String, accessKey: String, file: InputStream, channel: Channel<Message>) = coroutineScope {
    val stageHolder = StageHolder(Message.Stage.INITIALIZATION)
    val rat = Rat { parser: DocumentParser -> parser.ctx.ptr }
    val task = launch { verify(documentId, accessKey, file, stageHolder, rat) }
    while (task.isActive) {
        delay(200)
        val ptr = rat.report()
        val progress = if (ptr != null) {
            (ptr.bodyPosition * 1.0 / ptr.totalChildSize).let { if (it.isNaN()) 0.0 else it }
        } else 0.0
        channel.send(Message(documentId, Message.Code.INFO, stageHolder.stage, "PROGRESS: $progress"))
    }
    task.invokeOnCompletion {
        channel.close()
    }
}

suspend fun verify(
    documentId: String,
    accessKey: String,
    file: InputStream,
    stageHolder: StageHolder,
    rat: Rat<DocumentParser, Context.Pointer>
) = coroutineScope {
    val parser = DocumentParser() with rat
    withContext(Dispatchers.IO) { parser.load(file) }
    stageHolder.stage = Message.Stage.VERIFICATION
    parser.runVerification()
    stageHolder.stage = Message.Stage.RENDERING
    val render = RenderLauncher(parser).render()

    stageHolder.stage = Message.Stage.SAVING
    val result = withContext(Dispatchers.IO) { ByteArrayOutputStream().also { parser.writeResult(it) } }

    S3.uploadRender(documentId, render.toByteArray(), accessKey)
    S3.uploadConclusion(documentId, result.toByteArray(), accessKey)
}