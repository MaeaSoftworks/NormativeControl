package normativecontrol.core.annotations

/**
 * Annotation that marks member as internal in [normativecontrol.core] module.
 */
@RequiresOptIn("This declaration is internal in core module.", level = RequiresOptIn.Level.ERROR)
internal annotation class CoreInternal
