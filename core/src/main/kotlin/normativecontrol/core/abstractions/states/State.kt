package normativecontrol.core.abstractions.states

interface State {
    val key: Key

    interface Key {
        fun createState(): State
    }

    fun reset() { }
}