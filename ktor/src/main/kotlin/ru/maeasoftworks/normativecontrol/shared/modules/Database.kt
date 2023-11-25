package ru.maeasoftworks.normativecontrol.shared.modules

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.FlowQuery
import org.komapper.core.dsl.query.Query
import org.komapper.core.dsl.query.QueryScope
import org.komapper.r2dbc.R2dbcDatabase
import ru.maeasoftworks.normativecontrol.shared.utils.Service

class Database(override val di: DI): Service() {
    private var database: R2dbcDatabase

    init {
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
    }

    suspend fun <T> runQuery(query: QueryScope.() -> Query<T>): T = database.runQuery(query)

    fun <T> flowQuery(query: QueryScope.() -> FlowQuery<T>): Flow<T> = database.flowQuery(query)
}