package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.channels.Channel
import ru.maeasoftworks.normativecontrol.api.students.dto.Message

class MessageSender(private val channel: Channel<Message>) {
    suspend fun send(body: Message) {
        channel.send(body)
    }
}