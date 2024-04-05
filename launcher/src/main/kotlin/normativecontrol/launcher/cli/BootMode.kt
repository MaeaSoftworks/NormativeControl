package normativecontrol.launcher.cli

import normativecontrol.launcher.Lambda.Configuration

sealed class BootMode {
    data class Lambda(val configuration: Configuration) : BootMode()

    data class Client(val isBlocking: Boolean) : BootMode()

    data object Help : BootMode()
}