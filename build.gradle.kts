plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.emortal.minestom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://repo.emortal.dev/snapshots")
    maven("https://repo.emortal.dev/releases")

    maven("https://jitpack.io")
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("dev.hollowcube:polar:1.3.1")
    implementation("com.github.EmortalMC:NBStom:b03d6a032a")
    implementation("com.github.EmortalMC:rayfast:9e5accbdfd")

    implementation("dev.emortal.minestom:core:9c52249")
    implementation("dev.emortal.minestom:game-sdk:54c70c8")

    implementation("net.kyori:adventure-text-minimessage:4.14.0")

//    implementation("dev.emortal.api:kurushimi-sdk:e4d7b15") { // should be provided by core
//        exclude(group = "dev.emortal.api", module = "grpc-sdk")
//    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf(
                "--release", "20",
                "--enable-preview"
        ))
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        mergeServiceFiles()

        manifest {
            attributes(
                "Main-Class" to "dev.emortal.minestom.lobby.Entrypoint",
                "Multi-Release" to true
            )
        }
    }

    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    build { dependsOn(shadowJar) }
}