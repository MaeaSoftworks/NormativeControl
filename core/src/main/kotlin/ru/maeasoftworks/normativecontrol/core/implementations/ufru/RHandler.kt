package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization

@EagerInitialization
object RHandler : Handler<R>({ register<R>(Profile.UrFU) { RHandler } }) {
    override suspend fun handle(element: Any) {

    }
}