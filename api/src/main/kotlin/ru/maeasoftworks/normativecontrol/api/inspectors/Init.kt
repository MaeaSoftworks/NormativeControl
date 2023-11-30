package ru.maeasoftworks.normativecontrol.api.inspectors

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.InspectorAccountController
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.InspectorViewController
import ru.maeasoftworks.normativecontrol.api.inspectors.services.InspectorAccountService

fun DI.MainBuilder.initializeInspectorModule() {
    bindSingleton { InspectorViewController(this.di) }
    bindSingleton { InspectorAccountController(this.di) }
    bindSingleton { InspectorAccountService(this.di) }
}