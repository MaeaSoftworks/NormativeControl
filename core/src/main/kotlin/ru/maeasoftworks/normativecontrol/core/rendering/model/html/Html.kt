package ru.maeasoftworks.normativecontrol.core.rendering.model.html

import ru.maeasoftworks.normativecontrol.core.rendering.model.css.Stylesheet

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

        suspend fun body(content: suspend () -> HtmlElement) {
            html.content.add(content())
        }
    }
}

suspend fun html(builder: suspend Html.Builder.() -> Unit): Html {
    val h = Html.Builder()
    h.builder()
    return h.html
}