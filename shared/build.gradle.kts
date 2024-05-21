plugins {
    kotlin("jvm") version "2.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.slf4j:slf4j-api:2.0.12")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}