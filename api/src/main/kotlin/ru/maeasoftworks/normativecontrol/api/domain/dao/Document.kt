package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*

@KomapperEntity(["documents"])
@KomapperTable("documents")
@KomapperManyToOne(
    targetEntity = User::class,
    navigator = "user"
)
data class Document(
    @KomapperId
    val id: String,
    val userId: String
)