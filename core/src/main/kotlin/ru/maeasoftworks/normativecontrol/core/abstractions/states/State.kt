package ru.maeasoftworks.normativecontrol.core.abstractions.states

interface State {
    val key: Key

    interface Key
}