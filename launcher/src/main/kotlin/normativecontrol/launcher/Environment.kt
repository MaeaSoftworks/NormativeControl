package normativecontrol.launcher

@Suppress("unused")
typealias environment = Environment

object Environment {
    fun variable(path: String): String {
        return System.getenv()[path] ?: throw Exception("Required environment variable $path not set.")
    }

    fun optionalVariable(path: String): String? {
        return System.getenv()[path]
    }
}