package com.fourthwall.cinema.manager

import io.ktor.server.config.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun configureDatabases(config: ApplicationConfig) {
    Database.connect(
        driver = "org.postgresql.Driver",
        url = config.propertyOrNull("databases.cinemamanagerdb.url")
            ?.getString() ?: "",
        user = config.propertyOrNull("databases.cinemamanagerdb.user")
            ?.getString() ?: "",
        password = config.propertyOrNull("databases.cinemamanagerdb.password")
            ?.getString() ?: "",
    )
}