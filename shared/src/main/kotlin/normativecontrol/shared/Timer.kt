package normativecontrol.shared

inline fun <T> timer(out: (Long) -> Unit, body: () -> T): T {
    val start = System.currentTimeMillis()
    val result = body()
    val end = System.currentTimeMillis()
    out(end - start)
    return result
}