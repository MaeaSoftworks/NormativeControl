plugins {
    id("application")
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.spring") version "1.7.0"
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.jpa") version "1.7.0"
}

application {
    mainClass.set("com.maeasoftworks.tellurium.NormativeControlApplication")
}

group = "com.maeasoftworks"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":polonium"))
    implementation(project(":livermorium"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.postgresql:postgresql")

    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.7")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
    implementation("jakarta.activation:jakarta.activation-api:2.1.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.0")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")

    developmentOnly("org.springframework.boot:spring-boot-starter-actuator")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}