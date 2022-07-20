plugins {
    kotlin("jvm") version "1.7.0"
    id("java-library")
}

group = "com.maeasoftworks"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.7")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("jakarta.activation:jakarta.activation-api:2.1.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}