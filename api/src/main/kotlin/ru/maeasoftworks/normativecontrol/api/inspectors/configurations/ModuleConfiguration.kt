package ru.maeasoftworks.normativecontrol.api.inspectors.configurations

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.maeasoftworks.normativecontrol.api.app.Controller
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.InspectorAccountController
import ru.maeasoftworks.normativecontrol.api.inspectors.controllers.InspectorViewController

@Suppress("unused")
@Module
interface ModuleConfiguration {
    @Binds
    @IntoSet
    fun inspectorViewController(impl: InspectorViewController): Controller

    @Binds
    @IntoSet
    fun inspectorAccountController(impl: InspectorAccountController): Controller
}