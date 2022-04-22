import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "dev.sebastiano"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.arkivanov.decompose:decompose:0.6.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.6.0")
    implementation("com.twitter:twitter-api-java-sdk:1.1.4")
    implementation("io.github.chozzle:compose-macos-theme-desktop:0.4.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.alialbaali.kamel:kamel-image:0.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}
