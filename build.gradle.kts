plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

val publicationVersion = "1.0"
group = "me.santio.coffee"
version = "1.0"

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "java")

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "me.santio.coffee"
                artifactId = project.name
                version = publicationVersion

                from(components["java"])
            }
        }
    }
}