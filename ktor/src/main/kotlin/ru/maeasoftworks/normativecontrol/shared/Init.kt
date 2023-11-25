package ru.maeasoftworks.normativecontrol.shared

import io.ktor.server.application.Application
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.singleton
import ru.maeasoftworks.normativecontrol.shared.modules.Database
import ru.maeasoftworks.normativecontrol.shared.modules.JWTService
import ru.maeasoftworks.normativecontrol.shared.modules.S3
import ru.maeasoftworks.normativecontrol.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.shared.services.RefreshTokenService

fun DI.MainBuilder.initializeSharedModule(application: Application) {
    JWTService(application).also { bind<JWTService>() with singleton { it } }
    Database(application).also { bindSingleton { it } }
    S3(application).also { bindSingleton { it } }

    bindSingleton { RefreshTokenService(this.di) }
    bindSingleton { RefreshTokenRepository(this.di) }
    bindSingleton { UserRepository(this.di) }
}