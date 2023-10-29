plugins {
    application
    kotlin("jvm") version "1.9.20-RC2"
    kotlin("plugin.serialization") version "1.9.10"
}

java.sourceCompatibility = JavaVersion.VERSION_21

application {
    mainClass.set("com.maeasoftworks.normativecontrolcore.bootstrap.NormativeControlApplicationKt")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}