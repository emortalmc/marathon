plugins {
    java
    application
    id("com.gradleup.shadow") version "9.0.0-beta15"
}

group = "dev.emortal.minestom"
version = "1.0-SNAPSHOT"
application.mainClass = "dev.emortal.minestom.marathon.Main"

repositories {
    mavenCentral()

    maven("https://repo.emortal.dev/snapshots")
    maven("https://repo.emortal.dev/releases")

    maven("https://jitpack.io")
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("dev.emortal.minestom:game-sdk:af050c7") {
        exclude(group = "dev.emortal.api", module = "common-proto-sdk")
    }

    implementation("dev.emortal.api:common-proto-sdk:2443a0a")
    implementation("net.kyori:adventure-text-minimessage:4.20.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    shadowJar {
        mergeServiceFiles()

        manifest {
            attributes(
                "Multi-Release" to true
            )
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    build {
        dependsOn(shadowJar)
    }
}
