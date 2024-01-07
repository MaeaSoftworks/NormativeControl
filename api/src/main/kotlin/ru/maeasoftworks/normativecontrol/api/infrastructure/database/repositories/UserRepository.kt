package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.dao._User
import ru.maeasoftworks.normativecontrol.api.domain.dao.users

object UserRepository : CrudRepository<User, String, _User>(Meta.users, Meta.users.id) {
    suspend fun getUserByEmail(email: String): User? = getBy(Meta.users.email, email)

    suspend fun existById(id: String): Boolean = getById(id) != null
}