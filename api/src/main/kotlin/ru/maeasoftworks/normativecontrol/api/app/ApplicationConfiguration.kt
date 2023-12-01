package ru.maeasoftworks.normativecontrol.api.app

import dagger.BindsInstance
import dagger.Component
import io.ktor.server.application.Application
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ru.maeasoftworks.normativecontrol.api.inspectors.configurations.ModuleConfiguration::class,
        ru.maeasoftworks.normativecontrol.api.shared.configurations.ModuleConfiguration::class,
        ru.maeasoftworks.normativecontrol.api.students.configurations.ModuleConfiguration::class
    ]
)
interface ApplicationConfiguration {
    val initializer: ControllerInitializer

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationConfiguration
    }
}