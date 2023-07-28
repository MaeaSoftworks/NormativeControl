package com.maeasoftworks.server.configs

object Shared {
    private var isSet = false
    var bucket: String = ""
        set(value) {
            if (isSet) throw Exception("Multiple assignments are forbidden")
            isSet = true
            field = value
        }
}