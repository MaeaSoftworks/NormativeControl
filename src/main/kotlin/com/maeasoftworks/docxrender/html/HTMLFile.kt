package com.maeasoftworks.docxrender.html

import com.maeasoftworks.docxrender.PIXELS_IN_POINT
import com.maeasoftworks.docxrender.PageSettings

class HTMLFile(
    settings: PageSettings
) {
    val content: MutableList<HTMLElement> = mutableListOf()
    val style: GlobalStyle = GlobalStyle()

    init {
        style["*"] = {
            "border" to "1px solid red"
            "box-sizing" to "border-box"
            "margin" to "0"
            "padding" to "0"
        }
        style[".page"] = {
            "width" to "${settings.width / PIXELS_IN_POINT}px"
            "min-height" to "${settings.height / PIXELS_IN_POINT}px"
            "padding-top" to "${settings.topMargin / PIXELS_IN_POINT}px"
            "padding-left" to "${settings.leftMargin / PIXELS_IN_POINT}px"
            "padding-bottom" to "${settings.bottomMargin / PIXELS_IN_POINT}px"
            "padding-right" to "${settings.rightMargin / PIXELS_IN_POINT}px"
        }
        style[".page-size"] = {
            "position" to "absolute"
            "border" to "1px solid blue"
            "box-sizing" to "border-box"
            "z-index" to "-10"
            "width" to "${settings.width / PIXELS_IN_POINT - settings.leftMargin / PIXELS_IN_POINT - settings.rightMargin / PIXELS_IN_POINT}px"
            "height" to "${settings.height / PIXELS_IN_POINT - settings.topMargin / PIXELS_IN_POINT - settings.bottomMargin / PIXELS_IN_POINT}px"
        }
    }

    override fun toString(): String {
        return "<!doctype html><html><head><style>$style</style></head><body>${content.joinToString("") { it.toString() }}</body></html>"
    }
}
