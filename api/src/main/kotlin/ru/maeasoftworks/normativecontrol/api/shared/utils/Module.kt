package ru.maeasoftworks.normativecontrol.api.shared.utils

import io.ktor.server.application.Application

interface Module {
    fun Application.module()
}