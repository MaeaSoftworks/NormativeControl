package ru.maeasoftworks.normativecontrol.api.infrastructure.utils

@RequiresOptIn("Needs to be wrapped with transaction function", RequiresOptIn.Level.WARNING)
annotation class NeedTransaction

object Transaction