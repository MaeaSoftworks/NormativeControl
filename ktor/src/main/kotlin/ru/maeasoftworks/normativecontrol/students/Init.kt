package ru.maeasoftworks.normativecontrol.students

import io.ktor.server.application.Application
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.students.controllers.StudentsController
import ru.maeasoftworks.normativecontrol.students.model.Verifier
import ru.maeasoftworks.normativecontrol.students.services.StudentDocumentService

fun DI.MainBuilder.initializeStudentModule(application: Application) {
    bindSingleton { StudentsController(this.di) }
    bindSingleton { StudentDocumentService(this.di) }
    bindSingleton { Verifier(this.di) }
}