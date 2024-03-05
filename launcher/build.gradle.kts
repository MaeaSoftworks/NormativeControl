plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    implementation("software.amazon.awssdk:s3:2.21.37")
    implementation("commons-cli:commons-cli:1.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("com.rabbitmq:amqp-client:5.20.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}