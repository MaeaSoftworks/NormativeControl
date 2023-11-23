package ru.maeasoftworks.normativecontrol.shared.utils

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias Pipe = PipelineContext<Unit, ApplicationCall>