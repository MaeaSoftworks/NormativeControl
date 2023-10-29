plugins {
    application
    kotlin("jvm") version "1.9.10"
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

java.sourceCompatibility = JavaVersion.VERSION_20

application {
    mainClass.set("bootstrapper.BootstrapperKt")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("software.amazon.awssdk:s3:2.20.121")

    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9")

    implementation("org.springframework.boot:spring-boot-starter-amqp")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}