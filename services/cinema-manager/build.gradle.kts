plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

dependencies {
    val exposedVersion: String by project
    val ktorVersion: String by project
    val ktorSwaggerVersion: String by project
    val logbackVersion: String by project
    val postgresqlVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:$ktorSwaggerVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("io.arrow-kt:arrow-core:1.2.4")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation(project(":infrastructure:moviedb-client"))
    implementation(project(":infrastructure:eventbus"))
    implementation(project(":shared:domain"))

    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-cio:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-serialization:$ktorVersion")
}

application {
    mainClass.set("com.fourthwall.cinema.manager.CinemaManagerKt")
}