package com.maeasoftworks.docxrender.model.html

import com.maeasoftworks.docxrender.PIXELS_IN_POINT
import com.maeasoftworks.docxrender.model.PageSettings

class HTMLFile(
    settings: PageSettings
) {
    var content: MutableList<HTMLElement> = mutableListOf()
    val style: GlobalStyle = GlobalStyle()

    init {
        style["*"] = {
            "box-shadow" set "inset 0px 0px 0px 0.5px red"
            "box-sizing" set "border-box"
            "margin" set "0"
            "padding" set "0"
        }
        style[".page"] = {
            "width" to settings.width / PIXELS_IN_POINT with "px"
            "min-height" to settings.height / PIXELS_IN_POINT with "px"
            "padding-top" to settings.topMargin / PIXELS_IN_POINT with "px"
            "padding-left" to settings.leftMargin / PIXELS_IN_POINT with "px"
            "padding-bottom" to settings.bottomMargin / PIXELS_IN_POINT with "px"
            "padding-right" to settings.rightMargin / PIXELS_IN_POINT with "px"
        }
        style[".page-size"] = {
            "position" set "absolute"
            "box-shadow" set "inset 0px 0px 0px 1px blue"
            "box-sizing" set "border-box"
            "z-index" set "-10"
            "width" to settings.width / PIXELS_IN_POINT - settings.leftMargin / PIXELS_IN_POINT - settings.rightMargin / PIXELS_IN_POINT with "px"
            "height" to settings.height / PIXELS_IN_POINT - settings.topMargin / PIXELS_IN_POINT - settings.bottomMargin / PIXELS_IN_POINT with "px"
        }
    }

    override fun toString(): String {
        return "<!doctype html><html><head><style>$style</style></head><body>${content.joinToString("") { it.toString() }}</body></html>"
    }
}
