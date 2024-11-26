package com.fourthwall.cinema.manager

import com.fourthwall.cinema.manager.api.v1.authRoutes
import com.fourthwall.cinema.manager.domain.administration.AuthorizationDomain
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
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
            configureDatabases(environment.config)

        routing {
            route("api.json") {

            authRoutes(authorizationDomain)
            route("swagger") {
        }
    }

}