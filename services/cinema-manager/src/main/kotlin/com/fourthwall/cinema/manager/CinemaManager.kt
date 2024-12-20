package com.fourthwall.cinema.manager

import com.fourthwall.cinema.manager.api.v1.authRoutes
import com.fourthwall.cinema.manager.api.v1.cinemaRoutes
import com.fourthwall.cinema.manager.domain.administration.AuthorizationDomain
import com.fourthwall.cinema.manager.domain.cinema.CinemaDomain
import com.fourthwall.cinema.manager.infrastructure.repository.PostgresCinemaRepository
import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.eventbus.postgres.PostgresEventBus
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun configureApplicationDatabase(config: ApplicationConfig): Database {
    return Database.connect(
        driver = "org.postgresql.Driver",
        url = config.propertyOrNull("databases.cinemamanagerdb.url")
            ?.getString() ?: "",
        user = config.propertyOrNull("databases.cinemamanagerdb.user")
            ?.getString() ?: "",
        password = config.propertyOrNull("databases.cinemamanagerdb.password")
            ?.getString() ?: "",
    )
}

fun configureEventBus(config: ApplicationConfig): EventBus {
    return PostgresEventBus(
        clientId = "cinemamanager",
        jdbcUrl = config.propertyOrNull("databases.eventbusdb.url")
            ?.getString() ?: "",
        username = config.propertyOrNull("databases.eventbusdb.user")
            ?.getString() ?: "",
        password = config.propertyOrNull("databases.eventbusdb.password")
            ?.getString() ?: ""
    )
}

fun Application.module(
    database: Database = configureApplicationDatabase(environment.config),
    eventBus: EventBus = configureEventBus(environment.config)
) {

    install(SwaggerUI)
    install(ContentNegotiation) {
        json() // Enable JSON serialization
    }

    routing {
        route("api.json") {
            openApiSpec()
        }
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api/v1") {

            val authorizationDomain = AuthorizationDomain()
            val cinemaDomain = CinemaDomain(
                PostgresCinemaRepository(database),
                eventBus
            )

            authRoutes(authorizationDomain)
            cinemaRoutes(
                authorizationDomain,
                cinemaDomain
            )
        }
    }

}