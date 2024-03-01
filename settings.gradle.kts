plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "normative-control-core"
include("core")
include("rendering-test-server")
include("core-launcher")