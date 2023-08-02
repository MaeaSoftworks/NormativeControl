package com.maeasoftworks.normativecontrolcore.bootstrap.configurations

object ValueStorage {
    private var isSet = false
    var bucket: String = ""
        set(value) {
            if (isSet) throw Exception("Multiple assignments are forbidden")
            isSet = true
            field = value
        }
}