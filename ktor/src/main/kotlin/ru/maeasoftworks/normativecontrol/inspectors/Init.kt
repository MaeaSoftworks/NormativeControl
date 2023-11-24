package ru.maeasoftworks.normativecontrol.inspectors

import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.maeasoftworks.normativecontrol.inspectors.controllers.InspectorViewController
import ru.maeasoftworks.normativecontrol.inspectors.services.InspectorAccountService
import ru.maeasoftworks.normativecontrol.students.controllers.StudentsController
import ru.maeasoftworks.normativecontrol.students.model.Verifier
import ru.maeasoftworks.normativecontrol.students.services.StudentDocumentService

fun DI.MainBuilder.initializeInspectorModule(application: Application) {
    bindSingleton { InspectorViewController(this.di) }
    bindSingleton { InspectorAccountService(this.di) }
}