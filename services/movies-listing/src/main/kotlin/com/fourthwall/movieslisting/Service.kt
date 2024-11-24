package com.fourthwall.movieslisting

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8000) {
        install(SwaggerUI)


        routing {
            // add the routes for swagger-ui and api-spec
            route("api.json") {
                openApiSpec()
            }
            route("swagger") {
                swaggerUI("/api.json")
            }

            get("/") {
                call.respondText("Hello, world!")
            }
        }

        monitor.subscribe(ApplicationStopping) {
            // Action to perform before shutdown
            println("Application is stopping. Performing cleanup...")
            // Add your cleanup logic here
        }
    }.start(wait = true)
}
