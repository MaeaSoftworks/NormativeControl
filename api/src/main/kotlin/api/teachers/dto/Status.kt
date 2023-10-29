package api.teachers.dto

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    CREATED,
    UPDATED,
    DELETED
}