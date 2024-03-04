plugins {
    kotlin("jvm") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}