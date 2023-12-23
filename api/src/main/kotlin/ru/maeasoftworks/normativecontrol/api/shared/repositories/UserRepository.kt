package ru.maeasoftworks.normativecontrol.api.shared.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.shared.utils.CRUDRepository
import ru.maeasoftworks.normativecontrol.api.shared.dao.User
import ru.maeasoftworks.normativecontrol.api.shared.dao._User
import ru.maeasoftworks.normativecontrol.api.shared.dao.users

object UserRepository : CRUDRepository<User, Long, _User>(Meta.users, Meta.users.id) {
    suspend fun getUserByUsername(username: String): User? = getBy(Meta.users.username, username)
}