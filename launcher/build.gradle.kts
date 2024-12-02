plugins {
    application
    id("com.github.johnrengelman.shadow") version ("8.1.1")
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    kotlin("kapt")
}

java.sourceCompatibility = JavaVersion.VERSION_21

application {
    mainClass.set("normativecontrol.launcher.MainKt")
}

dependencies {
    implementation(project(":implementation"))
    implementation(project(":shared"))

    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("org.apache.logging.log4j:log4j-api:2.23.0")
    implementation("org.apache.logging.log4j:log4j-core:2.23.0")

    implementation("software.amazon.awssdk:s3:2.21.37")

    implementation("info.picocli:picocli:4.7.6")
    kapt("info.picocli:picocli-codegen:4.7.6")

    implementation("org.slf4j:slf4j-api:2.0.12")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.0")

    implementation("com.rabbitmq:amqp-client:5.20.0")

    runtimeOnly("org.postgresql:postgresql:42.7.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}