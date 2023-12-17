package ru.maeasoftworks.normativecontrol.core.annotations

@RequiresOptIn(message = "Use specialized builder functions.", level = RequiresOptIn.Level.WARNING)
@Target(AnnotationTarget.CONSTRUCTOR)
annotation class InternalConstructor