plugins {
    id("java-library")
    kotlin("jvm") version "1.7.0"
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation(project(":polonium"))
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:8.3.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}