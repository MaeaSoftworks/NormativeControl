package ru.maeasoftworks.normativecontrol.api

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

private const val variablePath = "ktor.profile"

private val logger = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    var profile = System.getenv()[variablePath]
    if (profile == null) {
        logger.warn("Environment variable '$variablePath' is not set. Forcing 'production' profile...")
        profile = "production"
    } else {
        logger.info("Set environment profile: '$profile'")
    }
    EngineMain.main(args + "-config=application-$profile.yaml")
}

fun Application.default() = daggerApplication(DaggerApplicationComponent::builder)

inline fun <DaggerComponentBuilder : ApplicationComponent.Builder> Application.daggerApplication(
    createComponentBuilder: () -> DaggerComponentBuilder,
    initComponent: (DaggerComponentBuilder) -> Unit = { }
) {
    val builder = createComponentBuilder()
    builder.application(this)
    initComponent(builder)
    builder.build().also { it.registrar.apply { register() } }
}