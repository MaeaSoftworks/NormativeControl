package ru.maeasoftworks.normativecontrol.inspectors

import io.ktor.server.application.Application
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.inspectors.controllers.InspectorViewController
import ru.maeasoftworks.normativecontrol.inspectors.services.InspectorAccountService

fun DI.MainBuilder.initializeInspectorModule(application: Application) {
    bindSingleton { InspectorViewController(this.di) }
    bindSingleton { InspectorAccountService(this.di) }
}