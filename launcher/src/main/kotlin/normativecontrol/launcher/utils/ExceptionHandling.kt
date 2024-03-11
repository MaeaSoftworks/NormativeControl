package normativecontrol.launcher.utils


inline fun <R> exceptionToNullable(fn: () -> R): R? {
    return try {
        fn()
    } catch (e: Exception) {
        null
    }
}