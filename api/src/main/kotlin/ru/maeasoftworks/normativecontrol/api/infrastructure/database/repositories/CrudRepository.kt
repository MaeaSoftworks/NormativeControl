package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import kotlinx.coroutines.flow.Flow
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.operator.count
import org.komapper.core.dsl.query.singleOrNull
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.Database
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

abstract class CrudRepository<E : Any, ID : Any, M : EntityMetamodel<E, ID, M>>(
    rawMeta: EntityMetamodel<E, ID, M>,
    private val idColumn: PropertyMetamodel<E, ID, ID>
) {
    @Suppress("UNCHECKED_CAST")
    private val meta = rawMeta as M

    context(Transaction)
    open suspend fun save(entity: E): E {
        return Database {
            runQuery {
                QueryDsl.insert(meta).single(entity)
            }
        }
    }

    context(Transaction)
    open suspend fun getById(id: ID): E? {
        return Database {
            runQuery {
                QueryDsl.from(meta).where { idColumn eq id }.singleOrNull()
            }
        }
    }

    context(Transaction)
    open suspend fun <C : Any> getBy(column: PropertyMetamodel<E, C, C>, value: C): E? {
        return Database {
            runQuery {
                QueryDsl.from(meta).where { column eq value }.singleOrNull()
            }
        }
    }

    context(Transaction)
    open suspend fun <C : Any> getAllBy(column: PropertyMetamodel<E, C, C>, value: C): Flow<E> {
        return Database {
            flowQuery {
                QueryDsl.from(meta).where { column eq value }
            }
        }
    }

    context(Transaction)
    open suspend fun existById(id: ID): Boolean {
        return Database {
            runQuery {
                QueryDsl.from(meta).where { idColumn eq id }.select(count())
            }
        }!! > 0
    }

    context(Transaction)
    open suspend fun <C : Any> existBy(column: PropertyMetamodel<E, C, C>, value: C): Boolean {
        return Database {
            runQuery {
                QueryDsl.from(meta).where { column eq value }.select(count())
            }
        }!! > 0
    }

    context(Transaction)
    open suspend fun update(id: ID, throwOnNotFound: (() -> Exception)? = null, fn: E.() -> Unit): E? {
        val entity = getById(id)
        if (throwOnNotFound != null && entity == null) throw throwOnNotFound()
        entity?.fn()
        return Database {
            entity?.let {
                runQuery {
                    QueryDsl.update(meta).single(it)
                }
            }
        }
    }

    context(Transaction)
    open suspend fun delete(id: ID): E? {
        return Database {
            runQuery {
                QueryDsl.delete(meta).where { idColumn eq id }.returning()
            }.firstOrNull()
        }
    }
}