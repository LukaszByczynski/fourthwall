plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"

}

repositories {
    mavenCentral()
}

dependencies {
    // Common dependencies can be added here
}

allprojects {
    group = "com.fourthwall"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
