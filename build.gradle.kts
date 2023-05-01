import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.omegaweapondev"
version = "2.1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(files("${projectDir}/libs/library-1.7.0.jar"))
    implementation("org.bstats:bstats-bukkit:3.0.1")
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(11)
}

tasks.withType<ShadowJar> {
    listOf(
        "com.tchristofferson",
        "org.bstats",
        "me.ou.library",
        "dev.dbassett",
        "com.zaxxer"
    ).forEach { relocate(it, "me.omegaweapondev.stylizer.libs.$it") }

    archiveFileName.set("${project.name}-${project.version}.jar")
}