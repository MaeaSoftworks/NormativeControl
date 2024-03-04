plugins {
    kotlin("jvm") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

dependencies {
    implementation(project(":core"))
    implementation(project(":external-api"))
    implementation(kotlin("reflect"))
    implementation("commons-cli:commons-cli:1.6.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}