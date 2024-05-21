plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    api(project(":core"))
    implementation(project(":shared"))
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:2.0.12")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest:5.8.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}