plugins {
    kotlin("jvm") version "1.9.22" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "ru.maeasoftworks"
    version = "1.0"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "20"
            freeCompilerArgs += listOf("-Xcontext-receivers")
        }
    }
}