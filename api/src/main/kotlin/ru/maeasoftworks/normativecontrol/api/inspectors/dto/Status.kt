package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    CREATED,
    UPDATED,
    DELETED
}