package normativecontrol.core.predefined

import normativecontrol.core.traits.Trait

interface TextContentTrait : Trait {
    val text: AbstractTextContentTraitImplementor
}