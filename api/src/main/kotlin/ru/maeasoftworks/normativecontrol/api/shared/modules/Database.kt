package ru.maeasoftworks.normativecontrol.api.shared.modules

import dagger.Module
import dagger.Provides
import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.r2dbc.R2dbcDatabase

@Module
class Database {
    companion object {
        @Provides
        fun initializeDatabase(application: Application): R2dbcDatabase {
            val database: R2dbcDatabase
            val url = application.environment.config.property("r2dbc.url").getString()
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
            return database
        }
    }
}