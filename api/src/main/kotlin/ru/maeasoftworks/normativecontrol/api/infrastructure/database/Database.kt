package ru.maeasoftworks.normativecontrol.api.infrastructure.database

import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import ru.maeasoftworks.normativecontrol.core.annotations.Internal

@OptIn(Internal::class)
object Database: Module {
    @Internal
    lateinit var instance: R2dbcDatabase
        private set

    override fun Application.module() {
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
        instance = database
    }

    inline operator fun <T> invoke(fn: R2dbcDatabase.() -> T): T = instance.fn()
}