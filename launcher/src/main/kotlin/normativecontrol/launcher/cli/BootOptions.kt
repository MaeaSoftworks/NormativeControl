package normativecontrol.launcher.cli

enum class BootOptions(val option: String, val description: String, val hasArg: Boolean, val argName: String? = null) {
    Help("h", "print this message", false),
    Verifier("v", "verify single file. Cannot be used without 'source' option.", false),
    Render("r", "create temp html rendering file and open it", false),
    Inline("i", "force style inlining in html tags in render file", false),
    Source("source", "file that need to be verified", true, "file"),
    Result("result", "path to result file. If not specified, it will be saved in same folder as source file.", true, "result");

    override fun toString(): String {
        return option
    }
}