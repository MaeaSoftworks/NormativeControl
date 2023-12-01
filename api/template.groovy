yield """package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.routing.routing

@dagger.Module
interface Modules {
"""
controllers.eachWithIndex { controller, num ->
    yield """
    @dagger.Binds
    @dagger.multibindings.IntoSet
    fun controller${num}(impl: ${controller}): ru.maeasoftworks.normativecontrol.api.shared.utils.Controller
"""
}
yield '}'

yield """
@javax.inject.Singleton
@dagger.Component(modules = [
    Modules::class,
    """
modules.each { module ->
    yield """${module}::class,
"""
}
yield """
])
interface ApplicationComponent {
    val registrar: ControllerRegistrar

    @dagger.Component.Builder
    interface Builder {
        @dagger.BindsInstance
        fun application(application: io.ktor.server.application.Application): Builder

        fun build(): ApplicationComponent
    }
}"""
yieldUnescaped """
class ControllerRegistrar @javax.inject.Inject constructor(private val controllers: Set<@JvmSuppressWildcards ru.maeasoftworks.normativecontrol.api.shared.utils.Controller>) {
    fun io.ktor.server.application.Application.register() {
        routing {
            controllers.forEach { controller ->
                controller.apply { registerRoutes() }
            }
        }
    }
}
"""