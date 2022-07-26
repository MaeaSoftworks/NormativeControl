package com.maeasoftworks.livermorium.model.html

import com.maeasoftworks.livermorium.model.PageSettings
import com.maeasoftworks.livermorium.rendering.projectors.AutoHyphenProjector
import com.maeasoftworks.livermorium.utils.PIXELS_IN_POINT

class HTMLFile(
    settings: PageSettings
) {
    var content: MutableList<HTMLElement> = mutableListOf()
    private val style: GlobalStyle = GlobalStyle()

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
            "hyphens" to settings.autoHyphen with AutoHyphenProjector
        }
        style[".page-size"] = {
            "position" set "absolute"
            "box-shadow" set "inset 0px 0px 0px 1px blue"
            "box-sizing" set "border-box"
            "z-index" set "-10"
            "width" to (settings.width - settings.leftMargin - settings.rightMargin) / PIXELS_IN_POINT with "px"
            "height" to (settings.height - settings.topMargin - settings.bottomMargin) / PIXELS_IN_POINT with "px"
        }
    }

    override fun toString(): String {
        return "<!doctype html><html><head><style>$style</style></head><body>${content.joinToString("") { it.toString() }}</body></html>"
    }
}
