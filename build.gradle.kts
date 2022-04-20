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

val ktorVersion = "2.0.0"
dependencies {
    implementation(compose.desktop.currentOs)
    implementation("ch.qos.logback:logback-classic:1.2.10")
    implementation("com.arkivanov.decompose:decompose:0.6.0")
    implementation("com.github.twitch4j:twitch4j:1.9.0")
    implementation("io.github.chozzle:compose-macos-theme-desktop:0.4.2")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
