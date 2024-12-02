package normativecontrol.core.traits

/**
 * Complex repeatable logic used by multiple handlers.
 * Implementors can be abstract.
 * @param T Trait of this implementor
 * @sample normativecontrol.core.predefined.AbstractTextContentTraitImplementor
 */
interface Implementor<out T : Trait>