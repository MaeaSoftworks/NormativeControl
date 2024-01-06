plugins {
    kotlin("jvm") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
}

java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":hotloader"))
    ksp(project(":hotloader"))
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9")
    constraints {
        implementation("commons-codec:commons-codec:1.16.0") {
            because("Cxeb68d52e-5509 3.7 Exposure of Sensitive Information to an Unauthorized Actor vulnerability")
        }
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

ksp {
    arg("hotloader.basePackage", "ru.maeasoftworks.normativecontrol.core")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}