package com.maeasoftworks.normativecontrolcore.rendering.model.html

import com.maeasoftworks.normativecontrolcore.rendering.model.PageSettings
import com.maeasoftworks.normativecontrolcore.rendering.model.css.GlobalStyle
import com.maeasoftworks.normativecontrolcore.rendering.model.css.properties.*

class HTMLFile(private val settings: PageSettings) {
    var content: MutableList<HTMLElement> = mutableListOf()
    private val style: GlobalStyle = GlobalStyle()
    private val contentString: String
        get() = content.joinToString("") { it.toString() }

    init {
        createDefaultStyles()
    }

    private fun createDefaultStyles() {
        style += {
            "*" += {
                BoxShadow set "inset 0px 0px 0px 0.5px red"
                BoxSizing set "border-box"
                Margin set 0.0
                Padding set 0.0
            }

            ".page" += {
                Width set (settings.width).toDouble()
                MinHeight set (settings.height).toDouble()
                PaddingTop set (settings.topMargin).toDouble()
                PaddingLeft set (settings.leftMargin).toDouble()
                PaddingBottom set (settings.bottomMargin).toDouble()
                PaddingRight set (settings.rightMargin).toDouble()
                Hyphens set settings.autoHyphen
            }

            ".page-size" += {
                BoxShadow set "inset 0px 0px 0px 1px blue"
                BoxSizing set "border-box"
                Position set "absolute"
                Width set (settings.width - settings.leftMargin - settings.rightMargin).toDouble()
                Height set (settings.height - settings.topMargin - settings.bottomMargin).toDouble()
                ZIndex set -10
            }
        }
    }

    override fun toString(): String {
        return "<!doctype html><html><head><style>$style</style></head><body>$contentString</body></html>"
    }
}
