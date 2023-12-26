package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import kotlinx.coroutines.flow.Flow
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.query.firstOrNull
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database

abstract class CrudRepository<E : Any, ID : Any, M : EntityMetamodel<E, ID, M>>(
    rawMeta: EntityMetamodel<E, ID, M>,
    private val idColumn: PropertyMetamodel<E, ID, ID>
) {
    @Suppress("UNCHECKED_CAST")
    private val meta = rawMeta as M

    open suspend fun save(entity: E): E {
        return Database {
            withTransaction {
                runQuery {
                    QueryDsl.insert(meta).single(entity)
                }
            }
        }
    }

    open suspend fun getById(id: ID): E? {
        return Database {
            withTransaction {
                runQuery {
                    QueryDsl.from(meta).where { idColumn eq id }.firstOrNull()
                }
            }
        }
    }

    open suspend fun <C : Any> getBy(column: PropertyMetamodel<E, C, C>, value: C): E? {
        return Database {
            withTransaction {
                return@withTransaction runQuery {
                    QueryDsl.from(meta).where { column eq value }.firstOrNull()
                }
            }
        }
    }

    open suspend fun <C : Any> getAllBy(column: PropertyMetamodel<E, C, C>, value: C): Flow<E> {
        return Database {
            withTransaction {
                return@withTransaction flowQuery {
                    QueryDsl.from(meta).where { column eq value }
                }
            }
        }
    }

    open suspend fun update(id: ID, fn: E.() -> Unit): E? {
        return Database {
            withTransaction {
                val entity = getById(id)
                entity?.fn()
                return@withTransaction entity?.let {
                    runQuery {
                        QueryDsl.insert(meta).single(it)
                    }
                }
            }
        }
    }

    open suspend fun delete(id: ID): E? {
        return Database {
            withTransaction {
                return@withTransaction runQuery {
                    QueryDsl.delete(meta).where { idColumn eq id }.returning()
                }.firstOrNull()
            }
        }
    }
}