import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0-beta01"
}

group = "dev.sebastiano"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("ch.qos.logback:logback-classic:1.4.1")
    implementation("com.arkivanov.decompose:decompose:0.8.0")
    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:0.8.0")
    implementation("com.twitter:twitter-api-java-sdk:2.0.2")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.json:json:20220320")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("io.github.chozzle:compose-macos-theme-desktop:0.4.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
    implementation("com.alialbaali.kamel:kamel-image:0.4.1")
    implementation("io.ktor:ktor-client-cio:2.1.1")
    implementation("com.twitter.twittertext:twitter-text:3.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}
