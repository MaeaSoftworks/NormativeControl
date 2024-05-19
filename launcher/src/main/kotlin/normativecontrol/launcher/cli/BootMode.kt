package normativecontrol.launcher.cli

sealed class BootMode {
    data class Lambda(val configuration: normativecontrol.launcher.Lambda.Configuration) : BootMode()

    data class Client(val configuration: normativecontrol.launcher.client.Client.Configuration) : BootMode()

    data object Help : BootMode()
}