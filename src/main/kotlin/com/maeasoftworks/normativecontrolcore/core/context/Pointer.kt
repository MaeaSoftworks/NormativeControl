package com.maeasoftworks.normativecontrolcore.core.context

class Pointer(size: Int) {
    var totalChildSize: Int = size
        internal set

    var bodyPosition = 0
        internal set

    var totalChildContentSize: Int = 0

    var childContentPosition = 0
        internal set

    internal var lastMistake = 0L

    fun moveNextChild() {
        bodyPosition++
    }

    fun moveNextChildContent() {
        bodyPosition++
    }

    fun resetChildContentPointer() {
        childContentPosition = 0
    }
}