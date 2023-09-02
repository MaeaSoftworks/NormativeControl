package core.rendering.model.html

import core.rendering.model.css.Stylesheet

class Html {
    var content: MutableList<HtmlElement> = mutableListOf()
    private var stylesheet: Stylesheet = Stylesheet()

    override fun toString(): String {
        return "<!doctype html><html><head><style>$stylesheet</style></head><body>${content.joinToString("") { it.toString() }}</body></html>"
    }

    class Builder {
        val html = Html()

        fun stylesheet(builder: Stylesheet.Builder.() -> Unit) {
            val b = Stylesheet.Builder()
            b.builder()
            html.stylesheet = b.build()
        }

        fun body(content: () -> HtmlElement) {
            html.content.add(content())
        }
    }
}

fun html(builder: Html.Builder.() -> Unit): Html {
    val h = Html.Builder()
    h.builder()
    return h.html
}