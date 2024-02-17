package ru.maeasoftworks.normativecontrol.renderingtestserver

import kotlinx.coroutines.channels.Channel
import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.p
import kotlinx.html.stream.createHTML

object RenderHolder {
    val channel: Channel<Event> = Channel()

    var render: String = createHTML().html {
        body {
            p {
                +"There is no render yet :("
            }
        }
    }
        private set

    suspend fun changeRender(newRender: String) {
        render = newRender
        channel.send(Event.RELOAD)
    }
}