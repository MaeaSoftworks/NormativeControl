plugins {
    kotlin("jvm") version "1.9.20-RC2"
    kotlin("plugin.serialization") version "1.9.10"
}

java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}