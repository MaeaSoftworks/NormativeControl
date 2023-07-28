import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("application")
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.0"
}

java.sourceCompatibility = JavaVersion.VERSION_19

application {
    mainClass.set("com.maeasoftworks.bootstrap.NormativeControlApplicationKt")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":rendering"))
    implementation("io.minio:minio:8.5.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "19"
    }
}