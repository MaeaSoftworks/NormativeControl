package com.maeasoftworks.livermorium.model.html

import com.maeasoftworks.livermorium.model.PageSettings
import com.maeasoftworks.livermorium.model.css.GlobalStyle
import com.maeasoftworks.livermorium.model.css.properties.*

/**
 * Main HTML object that represents entire DOM
 * @param settings document settings
 */
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
                Margin set 0
                Padding set 0
            }

            ".page" += {
                Width set settings.width
                MinHeight set settings.height
                PaddingTop set settings.topMargin
                PaddingLeft set settings.leftMargin
                PaddingBottom set settings.bottomMargin
                PaddingRight set settings.rightMargin
                Hyphens set settings.autoHyphen
            }

            ".page-size" += {
                BoxShadow set "inset 0px 0px 0px 1px blue"
                BoxSizing set "border-box"
                Position set "absolute"
                Width set settings.width - settings.leftMargin - settings.rightMargin
                Height set settings.height - settings.topMargin - settings.bottomMargin
                ZIndex set -10
            }
        }
    }

    override fun toString(): String {
        return "<!doctype html><html><head><style>$style</style></head><body>$contentString</body></html>"
    }
}
