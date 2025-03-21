plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}


group = "dev.kugge"
version = "0.0.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.bluecolored.de/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("de.bluecolored:bluemap-api:2.7.4")
}

tasks.compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(21)
}

tasks.processResources {
    filter { line -> line.replace("\${version}", project.version.toString()) }
}

tasks.shadowJar {
    archiveFileName.set("SignMarkers-${project.version}.jar")
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
