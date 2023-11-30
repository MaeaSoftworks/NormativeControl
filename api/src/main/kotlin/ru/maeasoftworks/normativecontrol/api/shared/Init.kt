package ru.maeasoftworks.normativecontrol.api.shared

import io.ktor.server.application.Application
import org.kodein.di.*
import ru.maeasoftworks.normativecontrol.api.shared.modules.JWTService
import ru.maeasoftworks.normativecontrol.api.shared.modules.S3
import ru.maeasoftworks.normativecontrol.api.shared.modules.initializeDatabase
import ru.maeasoftworks.normativecontrol.api.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.api.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.api.shared.services.DocumentService
import ru.maeasoftworks.normativecontrol.api.shared.services.RefreshTokenService

fun DI.MainBuilder.initializeSharedModule(application: Application) {
    bindEagerSingleton { initializeDatabase(application) }

    bindEagerSingleton { JWTService(this.di) }
    bindEagerSingleton { S3(this.di) }

    bindSingleton { DocumentService(this.di) }
    bindSingleton { RefreshTokenService(this.di) }
    bindSingleton { RefreshTokenRepository(this.di) }
    bindSingleton { UserRepository(this.di) }
}