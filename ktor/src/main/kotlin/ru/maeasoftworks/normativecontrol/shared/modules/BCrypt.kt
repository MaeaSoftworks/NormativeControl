package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.server.application.*

var bCryptStrength: Int = 10

fun Application.configureBCrypt() {
    bCryptStrength = this.environment.config.property("bcrypt.strength").getString().toInt()
}