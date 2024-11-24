plugins {
    kotlin("jvm")
}

dependencies {
    val ktorVersion: String by project
    val logbackVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.github.smiley4:ktor-swagger-ui:4.1.0")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}
