package normativecontrol.launcher

import normativecontrol.launcher.client.Client
import picocli.CommandLine.Command

@Command(
    name = "normative-control",
    subcommands = [
        Verifier::class,
        Client::class
    ],
    mixinStandardHelpOptions = true
)
class NormativeControl