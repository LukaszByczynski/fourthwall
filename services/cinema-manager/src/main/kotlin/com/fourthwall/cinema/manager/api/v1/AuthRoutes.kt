package com.fourthwall.cinema.manager.api.v1

import arrow.core.Either
import arrow.core.Ior
import com.fourthwall.cinema.manager.domain.administration.AuthorizationDomain
import com.fourthwall.cinema.manager.domain.administration.AuthorizationToken
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

fun Route.authRoutes(authorizationDomain: AuthorizationDomain) {
    post("auth/login", {
        description = "Logs in a user and returns an authorization token."
        request {
            body<LoginRequest>()
        }
        response {
            HttpStatusCode.OK to {
                description = "Successful login."
                body<AuthorizationToken>()
            }
            HttpStatusCode.Unauthorized to {
                description = "Wrong login or password."
                body<String>()
            }
        }
    }) {
        val loginRequest = call.receive<LoginRequest>()
        val result = authorizationDomain.login(
            loginRequest.username,
            loginRequest.password
        )
        when (result) {
            is Either.Right -> {
                call.respond(HttpStatusCode.OK, result)
            }

            is Either.Left -> {
                call.respond(HttpStatusCode.BadRequest, result.value)
            }
        }
    }

    post("/logout", {
        description = "Logs out a user by invalidating the token."
        request {
            body<AuthorizationToken>()
        }
        response {
            HttpStatusCode.OK to {
                description = "Successful logout."
            }
        }
    }) {
        val token = call.receive<AuthorizationToken>()
        authorizationDomain.logout(token)
        call.respond(HttpStatusCode.OK)
    }
}