package normativecontrol.core.rendering.css

import java.io.Serializable

/**
 * Representation of CSS stylesheet (collection of rulesets), e.g.:
 * ```css
 * div {
 *     display: block;
 *     background-color: white;
 * }
 *
 * body {
 *     display: grid;
 * }
 * ```
 */
class Stylesheet : Serializable {
    internal val rulesets: MutableMap<String, DeclarationBlock> = mutableMapOf()

    override fun toString(): String {
        return rulesets.toList().joinToString("") { "${it.first}{${it.second.toCSS()}}" }
    }

    /**
     * Joins rules from [another] ruleset to `this` ruleset.
     */
    internal fun join(another: Stylesheet) {
        rulesets.putAll(another.rulesets)
    }

    /**
     * Creates ruleset in this stylesheet using [CssDsl].
     * @receiver CSS selector
     * @param builder ruleset initialization
     * @see [CssDsl]
     */
    @CssDsl
    operator fun String.invoke(builder: context(DeclarationBlock, CssProperties) StyleBuilder.() -> Unit) {
        val declarationBlock = DeclarationBlock(noInline = true)
        builder(declarationBlock, CssProperties, StyleBuilder)
        rulesets[this] = declarationBlock
    }
}