package ru.maeasoftworks.normativecontrol.inspectors

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.inspectors.controllers.InspectorViewController
import ru.maeasoftworks.normativecontrol.inspectors.services.InspectorAccountService

fun DI.MainBuilder.initializeInspectorModule() {
    bindSingleton { InspectorViewController(this.di) }
    bindSingleton { InspectorAccountService(this.di) }
}