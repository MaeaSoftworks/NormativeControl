plugins {
    id("java-library")
    kotlin("jvm")
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation(project(":core"))
    // subprojects

    // logging
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.slf4j:slf4j-api:2.0.5")

    // docx4j
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.7")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("jakarta.activation:jakarta.activation-api:2.1.0")

    // testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
