package normativecontrol.core.settings

data class RenderingSettings(
    /**
     * If set to `false` all CSS styles will be added to minimized style,
     * otherwise it will be inlined into HTML elements
     */
    val forceStyleInlining: Boolean = false
)