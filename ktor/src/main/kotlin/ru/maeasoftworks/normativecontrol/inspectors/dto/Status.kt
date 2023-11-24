package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    CREATED,
    UPDATED,
    DELETED
}