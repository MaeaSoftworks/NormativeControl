package normativecontrol.core.utils


inline fun <T> timer(out: (String) -> Unit, body: () -> T): T {
    val start = System.currentTimeMillis()
    val result = body()
    val end = System.currentTimeMillis()
    out("${end - start}")
    return result
}

inline fun timer(out: (String) -> Unit, body: () -> Unit) {
    val start = System.currentTimeMillis()
    body()
    val end = System.currentTimeMillis()
    out("${end - start}")
}