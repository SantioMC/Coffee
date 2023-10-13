plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    `maven-publish`
}

group = "me.santio.coffee.jda"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("net.dv8tion:JDA:5.0.0-beta.15") {
        exclude("opus-java")
    }
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    shadowJar {
        archiveClassifier.set("")
    }
}