plugins {
    kotlin("jvm") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    implementation("commons-cli:commons-cli:1.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}