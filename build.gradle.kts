plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.ben-manes.versions") version "0.50.0"
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
        }
    }
}