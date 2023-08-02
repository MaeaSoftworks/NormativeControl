package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

object Position : Property<String>()

object ZIndex : IntProperty() {
    override fun toString(): String {
        return "z-index"
    }
}
