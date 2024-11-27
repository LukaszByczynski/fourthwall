plugins {
    kotlin("jvm")
//    kotlin("plugin.serialization")
    `java-library`
}

dependencies {
    val exposedVersion: String by project
    val ktorVersion: String by project
    val ktorSwaggerVersion: String by project
    val logbackVersion: String by project
    val postgresqlVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
    implementation("com.squareup.moshi:moshi-adapters:1.15.1")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
}


tasks.test {
    useJUnitPlatform()
}
