package ru.maeasoftworks.normativecontrol.core.annotations

/**
 * Read as `internal` visibility modifier.
 *
 * Alternative for `internal` visibility modifier for fields that are only used in `inline` functions or top-level functions due to Kotlin restrictions.
 */
@RequiresOptIn(message = "This declaration is marked as for internal use only.", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
annotation class Internal