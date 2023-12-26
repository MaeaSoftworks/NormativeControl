package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

import io.ktor.server.application.Application

interface Module {
    fun Application.module()
}