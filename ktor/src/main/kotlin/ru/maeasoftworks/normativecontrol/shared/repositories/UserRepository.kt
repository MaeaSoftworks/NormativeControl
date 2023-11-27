package ru.maeasoftworks.normativecontrol.shared.repositories

import org.kodein.di.DI
import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.shared.dao.User
import ru.maeasoftworks.normativecontrol.shared.dao._User
import ru.maeasoftworks.normativecontrol.shared.dao.users
import ru.maeasoftworks.normativecontrol.shared.utils.Repository

class UserRepository(override val di: DI) : Repository<User, Long, _User>(Meta.users, Meta.users.id) {
    suspend fun getUserByUsername(username: String): User? = getBy(Meta.users.username, username)
}