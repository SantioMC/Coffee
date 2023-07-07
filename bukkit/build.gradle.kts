plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
}

group = "me.santio.coffee.bukkit"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(project(":common"))
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
}