plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    val exposedVersion: String by project
    val logbackVersion: String by project
    val postgresqlVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:atomicfu:0.17.0")

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
}
