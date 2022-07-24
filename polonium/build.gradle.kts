plugins {
    id("java-library")
    kotlin("jvm") version "1.7.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    // logging
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha16")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")

    // docx4j
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.7")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("jakarta.activation:jakarta.activation-api:2.1.0")

    // testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}