import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = "com.maeasoftworks"
    version = "1.0"

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "20"
        }
    }
}