package com.maeasoftworks.docxrender.rendering.projectors

interface Projector {
    fun <T> project(from: T): String?
}