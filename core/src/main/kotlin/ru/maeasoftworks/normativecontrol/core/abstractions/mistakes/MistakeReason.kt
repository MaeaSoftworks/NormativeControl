package ru.maeasoftworks.normativecontrol.core.abstractions.mistakes

import kotlinx.serialization.Serializable

@Serializable(MistakeReasonSerializer::class)
interface MistakeReason {
    val description: String
}