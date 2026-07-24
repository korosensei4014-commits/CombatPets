plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    implementation(project(":API"))
    paperweight.paperDevBundle("26.1.2.build.+")
}