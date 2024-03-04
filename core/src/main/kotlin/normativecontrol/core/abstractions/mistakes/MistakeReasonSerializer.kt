package normativecontrol.core.abstractions.mistakes

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MistakeReasonSerializer : KSerializer<MistakeReason> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(MistakeReason::class.qualifiedName!!, PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: MistakeReason) = encoder.encodeString(value.description)
    override fun deserialize(decoder: Decoder): MistakeReason = throw UnsupportedOperationException("This serialization works in one way.")
}