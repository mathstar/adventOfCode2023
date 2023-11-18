import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "com.staricka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.github.jkcclemens:khttp:-SNAPSHOT")
    testImplementation(kotlin("test"))
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.staricka.adventofcode2023.framework.MainKt")
    applicationDefaultJvmArgs = listOf(
        "--add-opens", "java.base/java.net=ALL-UNNAMED",
        "--add-opens", "java.base/sun.net.www.protocol.https=ALL-UNNAMED"
    )
}