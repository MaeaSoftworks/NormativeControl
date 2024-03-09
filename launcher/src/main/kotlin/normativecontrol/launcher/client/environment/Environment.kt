package normativecontrol.launcher.client.environment

object Environment {
    operator fun get(path: String) = EnvironmentVariable(path)
}

val environment: Environment
    get() = Environment