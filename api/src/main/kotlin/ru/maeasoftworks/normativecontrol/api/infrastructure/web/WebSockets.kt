package ru.maeasoftworks.normativecontrol.api.infrastructure.web

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import java.time.Duration

object WebSockets : Module {
    var maxFrameSize: Long = 0
        private set

    override fun Application.module() {
        maxFrameSize = environment.config.property("ktor.websocket.maxFrameSize").getString().toLong()

        this.install(io.ktor.server.websocket.WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = this@WebSockets.maxFrameSize
            masking = false
        }
    }
}