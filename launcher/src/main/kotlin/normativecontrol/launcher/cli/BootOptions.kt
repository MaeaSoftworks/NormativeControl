package normativecontrol.launcher.cli

enum class BootOptions(val option: String, val description: String, val hasArg: Boolean, val argName: String? = null) {
    Help("h", "print this message", false),
    Lambda("l", "\"lambda mode\": verifies single file. Cannot be used without 'source' option.", false),
    Render("r", "create temp html rendering file and open it", false),
    Source("source", "file that need to be verified", true, "file"),
    Result("result", "path to result file. If not specified, it will be saved in same folder as source file.", true, "result"),
    Blocking("b", "Blocking mode for client", false)
}