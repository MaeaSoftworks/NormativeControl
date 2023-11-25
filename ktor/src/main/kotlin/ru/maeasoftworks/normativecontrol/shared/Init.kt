package ru.maeasoftworks.normativecontrol.shared

import org.kodein.di.*
import ru.maeasoftworks.normativecontrol.shared.modules.Database
import ru.maeasoftworks.normativecontrol.shared.modules.JWTService
import ru.maeasoftworks.normativecontrol.shared.modules.S3
import ru.maeasoftworks.normativecontrol.shared.repositories.RefreshTokenRepository
import ru.maeasoftworks.normativecontrol.shared.repositories.UserRepository
import ru.maeasoftworks.normativecontrol.shared.services.RefreshTokenService

fun DI.MainBuilder.initializeSharedModule() {
    bindEagerSingleton { JWTService(this.di) }
    bindEagerSingleton { Database(this.di) }
    bindEagerSingleton { S3(this.di) }

    bindSingleton { RefreshTokenService(this.di) }
    bindSingleton { RefreshTokenRepository(this.di) }
    bindSingleton { UserRepository(this.di) }
}