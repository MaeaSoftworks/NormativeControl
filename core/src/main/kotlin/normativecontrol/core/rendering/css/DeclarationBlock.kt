package normativecontrol.core.rendering.css

/**
 * Represents CSS declaration block, e.g.:
 * ```css
 * {
 *     display: block;
 *     background-color: white;
 * }
 * ```
 * @property classes List of generated classes for CSS code minification
 * @property noInline If set to `false` this declaration will be added to ruleset,
 * otherwise it will be inlined into HTML element
 * @see normativecontrol.core.settings.RenderingSettings.forceStyleInlining
 */
class DeclarationBlock(
    val classes: MutableList<String> = mutableListOf(),
    val noInline: Boolean = false
) {
    /**
     * Count of rules in this ruleset.
     */
    val ruleCount: Int
        get() = rules.size

    internal val rules: MutableList<Rule> by lazy { mutableListOf() }

    /**
     * Converts ruleset to CSS code.
     */
    fun toCSS(): String {
        val result = StringBuilder()
        for (rule in rules) {
            val r = rule.serialize()
            if (r != null) {
                result.append(r).append(";")
            }
        }

        return result.toString()
    }

    /**
     * Adds rules to this ruleset using [CssDsl].
     * @param rules set of rules created with [CssDsl]
     */
    operator fun plusAssign(rules: context(DeclarationBlock, CssProperties, StyleBuilder) () -> Unit) {
        rules(this, CssProperties, StyleBuilder)
    }

    /**
     * Joins rules of detached CSS ruleset to this ruleset.
     */
    fun apply(detached: Detached) {
        detached.declarationBlock(this, CssProperties, StyleBuilder)
    }

    companion object {
        /**
         * Creates new [Detached] (without classifiers) ruleset
         * @param initializer ruleset rules
         * @return detached ruleset
         */
        fun detached(initializer: context(DeclarationBlock, CssProperties, StyleBuilder) () -> Unit): Detached {
            return Detached(initializer)
        }
    }

    /**
     * CSS ruleset without classifiers that can be [applied][apply] to another ruleset.
     * @property declarationBlock Ruleset initializer
     */
    @JvmInline
    value class Detached internal constructor(val declarationBlock: context(DeclarationBlock, CssProperties, StyleBuilder) () -> Unit)
}