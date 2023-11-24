plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.3.6"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

group = "ru.maeasoftworks"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("reflect"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-config-yaml:2.3.6")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    ksp("org.komapper:komapper-processor")
    ksp(platform("org.komapper:komapper-platform:1.15.0"))
    implementation("org.komapper:komapper-starter-r2dbc")
    implementation("org.komapper:komapper-dialect-postgresql-r2dbc")
    implementation(platform("org.komapper:komapper-platform:1.15.0"))

    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("org.kodein.di:kodein-di-jvm:7.17.0")
    implementation("software.amazon.awssdk:s3:2.20.121")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("software.amazon.awssdk:netty-nio-client:2.20.121")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.20")

}

ksp {
    arg("komapper.enableEntityMetamodelListing", "true")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += listOf("-opt-in=org.komapper.annotation.KomapperExperimentalAssociation")
    }
}