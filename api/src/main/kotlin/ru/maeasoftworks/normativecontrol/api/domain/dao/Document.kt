package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperManyToOne
import org.komapper.annotation.KomapperTable
import java.time.Instant

@KomapperEntity(["documents"])
@KomapperTable("documents")
@KomapperManyToOne(
    targetEntity = User::class,
    navigator = "user"
)
data class Document(
    @KomapperId
    val id: String,
    val userId: String,
    val timestamp: Instant
)