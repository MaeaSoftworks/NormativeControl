package normativecontrol.core.utils

inline fun <T, R> Array<T>.flatMap(transform: (T) -> Array<R>): List<R> {
    val destination = ArrayList<R>()
    for (element in this) {
        val list = transform(element)
        destination.addAll(list)
    }
    return destination
}