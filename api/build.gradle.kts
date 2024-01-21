plugins {
    application
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_20

group = "ru.maeasoftworks"
version = "0.0.1"

application {
    mainClass.set("ru.maeasoftworks.normativecontrol.api.ApplicationKt")

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
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-request-validation-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    ksp("org.komapper:komapper-processor")
    ksp(platform("org.komapper:komapper-platform:1.15.0"))
    implementation("org.komapper:komapper-starter-r2dbc")
    implementation(platform("org.komapper:komapper-platform:1.15.0"))

    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("org.codehaus.janino:janino:3.1.11")
    implementation("software.amazon.awssdk:s3:2.21.37")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("software.amazon.awssdk:netty-nio-client:2.21.37")
    implementation("dev.reformator.stacktracedecoroutinator:stacktrace-decoroutinator-jvm:2.3.8")

    runtimeOnly("org.komapper:komapper-dialect-h2-r2dbc")
    // comment this line if it crashed in standalone mode
    runtimeOnly("org.komapper:komapper-dialect-postgresql-r2dbc")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("io.ktor:ktor-client-websockets")
    testImplementation("io.ktor:ktor-client-content-negotiation")

    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:2.0.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.komapper:komapper-dialect-h2-r2dbc")
}

ksp {
    arg("komapper.enableEntityMetamodelListing", "true")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-opt-in=org.komapper.annotation.KomapperExperimentalAssociation",
        "-Xcontext-receivers"
    )
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}