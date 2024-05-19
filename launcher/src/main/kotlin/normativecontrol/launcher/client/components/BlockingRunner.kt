package normativecontrol.launcher.client.components

class BlockingRunner : Runner {
    override fun run(runnable: Runnable) {
        runnable.run()
    }

    override fun close() {}
}