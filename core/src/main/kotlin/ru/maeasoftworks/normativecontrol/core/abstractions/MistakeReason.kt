package ru.maeasoftworks.normativecontrol.core.abstractions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(MistakeReasonSerializer::class)
abstract class MistakeReason(val description: String)

object MistakeReasonSerializer : KSerializer<MistakeReason> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(MistakeReason::class.qualifiedName!!, PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: MistakeReason) = encoder.encodeString(value.description)
    override fun deserialize(decoder: Decoder): MistakeReason = throw UnsupportedOperationException("This serialization works in one way.")
}