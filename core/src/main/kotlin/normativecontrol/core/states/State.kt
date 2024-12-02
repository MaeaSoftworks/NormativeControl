package normativecontrol.core.states

abstract class State {
    internal val suppressed = mutableSetOf<Int>()
}