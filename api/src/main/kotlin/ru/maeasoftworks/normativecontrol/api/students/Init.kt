package ru.maeasoftworks.normativecontrol.api.students

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.api.students.controllers.StudentsController
import ru.maeasoftworks.normativecontrol.api.students.model.Verifier

fun DI.MainBuilder.initializeStudentModule() {
    bindSingleton { StudentsController(this.di) }

    bindSingleton { Verifier(this.di) }
}