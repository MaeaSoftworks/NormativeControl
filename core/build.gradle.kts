plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("reflect"))
    implementation("org.reflections:reflections:0.10.2")
    api("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.10")
    constraints {
        implementation("org.apache.commons:commons-compress:1.26.0")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.slf4j:slf4j-api:2.0.12")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}