package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.server.application.*
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase

suspend fun Application.configureDatabase() {
    val url = this.environment.config.property("r2dbc.url").getString()
    val database = R2dbcDatabase(url)
    database.withTransaction {
        database.runQuery {
            QueryDsl.create(Meta.all())
        }
    }
}