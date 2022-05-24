package com.maeasoftworks.normativecontrol.utils

fun <T> MutableList<T>.smartAdd(item: T?) {
    if (item != null) {
        this.add(item)
    }
}