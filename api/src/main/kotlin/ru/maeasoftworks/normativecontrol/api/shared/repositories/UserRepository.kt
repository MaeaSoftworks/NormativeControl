package ru.maeasoftworks.normativecontrol.api.shared.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.app.Repository
import ru.maeasoftworks.normativecontrol.api.shared.dao.User
import ru.maeasoftworks.normativecontrol.api.shared.dao._User
import ru.maeasoftworks.normativecontrol.api.shared.dao.users
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() : Repository<User, Long, _User>(Meta.users, Meta.users.id) {
    suspend fun getUserByUsername(username: String): User? = getBy(Meta.users.username, username)
}