plugins {
    id("java")
    kotlin("jvm") version "1.9.0"
}

group = "me.santio.coffee"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
}