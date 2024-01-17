package ru.maeasoftworks.normativecontrol.api.infrastructure.database.repositories

import org.komapper.core.dsl.Meta
import ru.maeasoftworks.normativecontrol.api.domain.dao.User
import ru.maeasoftworks.normativecontrol.api.domain.dao._User
import ru.maeasoftworks.normativecontrol.api.domain.dao.users
import ru.maeasoftworks.normativecontrol.api.infrastructure.database.UnsafeDataAccess
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Transaction
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.IdentificationException

object UserRepository : CrudRepository<User, String, _User>(Meta.users, Meta.users.id) {
    context(Transaction)
    @UnsafeDataAccess("UserRepository.getById() is unsafe for caller user. Please use UserRepository.identify instead.")
    override suspend fun getById(id: String): User? = super.getById(id)

    context(Transaction) suspend fun getUserByEmail(email: String): User? = getBy(Meta.users.email, email)

    context(Transaction) suspend fun identify(id: String): User = super.getById(id) ?: throw IdentificationException("Profile not found")

    context(Transaction) suspend fun existUserByEmail(email: String): Boolean = existBy(Meta.users.email, email)
}