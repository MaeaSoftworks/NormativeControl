package normativecontrol.core.states

abstract class State {
    val suppressed = mutableSetOf<Int>()
}