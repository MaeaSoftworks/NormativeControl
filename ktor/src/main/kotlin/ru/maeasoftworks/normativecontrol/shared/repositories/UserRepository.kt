package ru.maeasoftworks.normativecontrol.shared.repositories

import org.kodein.di.DI
import org.kodein.di.instance
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.expression.SortItem
import org.komapper.core.dsl.query.firstOrNull
import ru.maeasoftworks.normativecontrol.shared.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.shared.dao.User
import ru.maeasoftworks.normativecontrol.shared.dao.refreshTokens
import ru.maeasoftworks.normativecontrol.shared.dao.users
import ru.maeasoftworks.normativecontrol.shared.modules.Database
import ru.maeasoftworks.normativecontrol.shared.utils.Repository

class UserRepository(override val di: DI): Repository() {
    private val database: Database by instance()

    suspend fun getUserByUsername(username: String): User? {
        return database.runQuery {
            QueryDsl.from(Meta.users).where { Meta.users.username eq username }.firstOrNull()
        }
    }

    suspend fun getUserById(userId: Long): User? {
        return database.runQuery {
            QueryDsl.from(Meta.users).where { Meta.users.id eq userId }.firstOrNull()
        }
    }

    suspend fun saveUser(user: User) {
        database.runQuery {
            QueryDsl.insert(Meta.users).single(user)
        }
    }

    suspend fun getUserAllRefreshTokens(userId: Long): Set<RefreshToken> {
        val store = database.runQuery {
            QueryDsl
                .from(Meta.users)
                .where { userId eq Meta.users.id }
                .innerJoin(Meta.refreshTokens) {
                    Meta.users.id eq Meta.refreshTokens.userId
                }.orderBy(SortItem.Column.Desc(Meta.refreshTokens.expiresAt))
                    .includeAll()
        }
        return store[Meta.refreshTokens]
    }
}