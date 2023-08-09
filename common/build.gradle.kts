plugins {
    application
    kotlin("jvm") version "1.9.0"
}

group = "me.santio.coffee"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("reflect"))
    compileOnly(kotlin("stdlib-jdk8"))
}

application {
    mainClass.set("me.santio.coffee.common.Coffee")
}