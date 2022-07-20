package com.maeasoftworks.livermorium.rendering.projectors

interface Projector {
    fun <T> project(from: T): String?
}