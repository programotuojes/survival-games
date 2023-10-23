import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "com.programotuojes.survival_games"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks {
    runServer {
        minecraftVersion("1.19.2")
    }
}
