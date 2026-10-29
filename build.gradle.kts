plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("com.gradleup.shadow") version "9.4.1"
}

// Shared configuration for all modules
allprojects {
    group = "su.nightexpress.combatpets" 
    version = "2.8.4"

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
    // Remove the "-all" suffix from the file name
    archiveClassifier.set("")

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