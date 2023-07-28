package com.maeasoftworks.rendering.model.css.properties

object Position : Property()

object ZIndex : Property() {
    override fun toString(): String {
        return "z-index"
    }
}
