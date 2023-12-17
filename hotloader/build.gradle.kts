plugins {
    kotlin("jvm") version "1.9.21"
}

group = "ru.maeasoftworks.normativecontrol"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.16")
    implementation("com.squareup:kotlinpoet:1.15.3")
    implementation("com.squareup:kotlinpoet-ksp:1.15.3")
    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9")
    constraints {
        implementation("commons-codec:commons-codec:1.16.0") {
            because("Cxeb68d52e-5509 3.7 Exposure of Sensitive Information to an Unauthorized Actor vulnerability")
        }
    }
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}