plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    val exposedVersion: String by project
    val logbackVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:atomicfu:0.17.0")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:postgresql:1.17.3")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
}
