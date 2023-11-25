package ru.maeasoftworks.normativecontrol.api.shared.dao

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "users")
class User : Persistable<Long> {
    @Id
    @Column("id")
    @get:JvmName("getIdKt")
    var id: Long? = null

    @Column("username")
    var username: String? = null

    @Column("password")
    lateinit var password: String

    override fun getId(): Long? {
        return this.id
    }

    override fun isNew(): Boolean {
        return this.id == null
    }
}
