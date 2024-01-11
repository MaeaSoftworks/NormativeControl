package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.dao._User
import ru.maeasoftworks.normativecontrol.api.domain.dao.users
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction

object UserRepository : CrudRepository<User, String, _User>(Meta.users, Meta.users.id) {
    context(Transaction)
    suspend fun getUserByEmail(email: String): User? = getBy(Meta.users.email, email)

    context(Transaction)
    suspend fun existUserByEmail(email: String): Boolean = existBy(Meta.users.email, email)
}