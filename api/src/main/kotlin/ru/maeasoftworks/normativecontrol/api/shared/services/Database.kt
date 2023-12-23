package ru.maeasoftworks.normativecontrol.api.shared.services

import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase

object Database {
    lateinit var database: R2dbcDatabase
        private set

    fun Application.configureDatabase() {
        val database: R2dbcDatabase
        val url = environment.config.property("r2dbc.url").getString()
        runBlocking {
            database = R2dbcDatabase(url)
            database.withTransaction {
                Meta.all().forEach {
                    database.runQuery {
                        QueryDsl.create(it)
                    }
                }
            }
        }
        Database.database = database
    }

    inline operator fun <T> invoke(fn: R2dbcDatabase.() -> T): T = database.fn()
}