package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.dao._User
import ru.maeasoftworks.normativecontrol.api.domain.dao.users

object UserRepository : CrudRepository<User, Long, _User>(Meta.users, Meta.users.id) {
    suspend fun getUserByUsername(username: String): User? = getBy(Meta.users.username, username)
}