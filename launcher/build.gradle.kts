plugins {
    application
    id("com.github.johnrengelman.shadow") version("8.1.1")
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

application {
    mainClass.set("normativecontrol.launcher.MainKt")
}

dependencies {
    implementation(project(":core"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")

    implementation("org.apache.logging.log4j:log4j-api:2.23.0")
    implementation("org.apache.logging.log4j:log4j-core:2.23.0")

    implementation("software.amazon.awssdk:s3:2.21.37")

    implementation("commons-cli:commons-cli:1.6.0")

    implementation("org.slf4j:slf4j-api:2.0.12")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.0")

    implementation("com.rabbitmq:amqp-client:5.20.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}