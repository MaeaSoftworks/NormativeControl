package ru.maeasoftworks.normativecontrol.api.shared.utils

import io.ktor.server.application.Application
import io.ktor.server.routing.Routing
import kotlinx.coroutines.flow.Flow
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.query.firstOrNull
import org.komapper.r2dbc.R2dbcDatabase

abstract class Component : DIAware {
    val application: Application by instance()
}

abstract class Controller : Component() {
    abstract fun Routing.registerRoutes()
}

abstract class Service : Component()

@Suppress("UNCHECKED_CAST")
abstract class Repository<E : Any, ID : Any, M : EntityMetamodel<E, ID, M>>(
    rawMeta: EntityMetamodel<E, ID, M>,
    private val idColumn: PropertyMetamodel<E, ID, ID>
) : Component() {
    protected val database: R2dbcDatabase by instance()
    private val meta = rawMeta as M

    open suspend fun save(entity: E): E {
        return database.withTransaction {
            database.runQuery {
                QueryDsl.insert(meta).single(entity)
            }
        }
    }

    open suspend fun getById(id: ID): E? {
        return database.withTransaction {
            database.runQuery {
                QueryDsl.from(meta).where { idColumn eq id }.firstOrNull()
            }
        }
    }

    open suspend fun <C : Any> getBy(column: PropertyMetamodel<E, C, C>, value: C): E? {
        return database.withTransaction {
            return@withTransaction database.runQuery {
                QueryDsl.from(meta).where { column eq value }.firstOrNull()
            }
        }
    }

    open suspend fun <C : Any> getAllBy(column: PropertyMetamodel<E, C, C>, value: C): Flow<E> {
        return database.withTransaction {
            return@withTransaction database.flowQuery {
                QueryDsl.from(meta).where { column eq value }
            }
        }
    }

    suspend fun update(id: ID, fn: E.() -> Unit): E? {
        return database.withTransaction {
            val entity = getById(id)
            entity?.fn()
            return@withTransaction entity?.let {
                database.runQuery {
                    QueryDsl.insert(meta).single(it)
                }
            }
        }
    }

    suspend fun delete(id: ID): E? {
        return database.withTransaction {
            return@withTransaction database.runQuery {
                QueryDsl.delete(meta).where { idColumn eq id }.returning()
            }.firstOrNull()
        }
    }
}