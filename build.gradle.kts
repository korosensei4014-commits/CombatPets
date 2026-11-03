plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("com.gradleup.shadow") version "9.4.1"
}

// Shared configuration for all modules
allprojects {
    group = "su.nightexpress.combatpets" 
    // CombatPets Reforged version: upstream version + "-rN". Bump N for each release.
    // (Releases up to v2.8.4-bedrock.17 used the old "-bedrock.N" scheme.)
    version = "2.8.4-r7"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.nightexpressdev.com/releases")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://mvn.lumine.io/repository/maven-public/")
        maven("https://repo.magmaguy.com/releases/")
        maven("https://central.sonatype.com/")
        maven("https://repo.opencollab.dev/main/") // Geyser / Floodgate / Cumulus
        maven("https://maven.enginehub.org/repo/") // WorldGuard / WorldEdit
    }

    dependencies {
        compileOnly("su.nightexpress.nightcore:main:2.16.7")
        compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(tasks.named("jar"))
                artifact(tasks.named("sourcesJar")) {
                    classifier = "sources"
                }
                artifactId = project.name
            }
        }
        repositories {
            maven {
                name = "nightexpress"
                url = uri("https://repo.nightexpressdev.com/releases")
                credentials {
                    username = System.getenv("REPOSILITE_USER")
                    password = System.getenv("REPOSILITE_PASSWORD")
                }
            }
        }
    }
}

// Root module specific dependencies
dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("io.lumine:Mythic-Dist:5.0.1-SNAPSHOT")
    compileOnly("com.magmaguy:FreeMinecraftModels:2.3.17")
    compileOnly("io.github.arcaneplugins:levelledmobs-plugin:4.0.3.1")

    // Bedrock Edition forms. Optional at runtime: only used when Floodgate or Geyser is installed.
    // Brings the Cumulus form API (org.geysermc.cumulus) transitively.
    compileOnly("org.geysermc.floodgate:api:2.2.4-SNAPSHOT")

    // WorldGuard region flags (pet-pvp, pet-summon). Optional at runtime.
    // Server APIs are excluded so they don't clash with the Paper API used by this project.
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") {
        exclude(group = "org.bukkit")
        exclude(group = "org.spigotmc")
        exclude(group = "io.papermc.paper")
        exclude(group = "com.destroystokyo.paper")
    }

    implementation(project(":API"))
    implementation(project(":nms-paper"))
    implementation(project(":nms-26-2"))
    implementation(project(":nms-26-3"))
}

// Root module tasks
tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    // Remove the "-all" suffix from the file name. Jar: CombatPets-Reforged-<version>.jar
    // (only the file / display name changes; the plugin is still called "CombatPets" on the server).
    archiveClassifier.set("")
    archiveBaseName.set("CombatPets-Reforged")

    mergeServiceFiles()
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

tasks.processResources {
    filesMatching("*.yml") {
        expand(
            "project" to project,
            "version" to project.version
        )
    }
}