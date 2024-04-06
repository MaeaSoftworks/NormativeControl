package normativecontrol.core.abstractions.states

interface State {
    fun reset() { }

    interface Key

    interface Factory<T: State> {
        fun build(): State
    }
}