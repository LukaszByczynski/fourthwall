plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-library`
}

dependencies {
    val ktorVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}