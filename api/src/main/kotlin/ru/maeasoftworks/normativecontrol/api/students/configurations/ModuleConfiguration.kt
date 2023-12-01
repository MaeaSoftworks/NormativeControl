package ru.maeasoftworks.normativecontrol.api.students.configurations

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import ru.maeasoftworks.normativecontrol.api.app.Controller
import ru.maeasoftworks.normativecontrol.api.students.controllers.StudentsController

@Suppress("unused")
@Module
interface ModuleConfiguration {
    @Binds
    @IntoSet
    fun studentsController(impl: StudentsController): Controller
}