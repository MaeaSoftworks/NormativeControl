package normativecontrol.core.abstractions.states

class UndefinedState: State {
    override val key: StateFactory = Companion

    companion object: StateFactory {
        override fun createState() = UndefinedState()
    }
}