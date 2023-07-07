import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    application
}

group = "me.santio.coffee"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    implementation(project(":common"))
}

application {
    mainClass.set("me.santio.coffee.Coffee")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<ShadowJar> {
        archiveFileName.set("Coffee.jar")
        archiveClassifier.set("")
        project.tasks.getByName("jar").enabled = false
    }
}