package com.fourthwall.cinema.manager.api.v1

import arrow.core.Either
import com.fourthwall.cinema.manager.domain.administration.AuthorizationDomain
import com.fourthwall.cinema.manager.domain.administration.AuthorizationToken
import com.fourthwall.cinema.manager.domain.cinema.*
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AddMovieRequest(val name: String, val imdbId: String)

@Serializable
data class AddShowTimeRequest(val date: String, val hour: Int, val minute: Int)

fun Route.cinemaRoutes(
    authorizationDomain: AuthorizationDomain,
    cinemaDomain: CinemaDomain
) {
    route("cinema") {
        post("/movies/add", {
            description = "Adds a new movie to the cinema"
            request {
                headerParameter<AuthorizationToken>("X-TOKEN") {
                    description = "Authorization token"
                }
                body<AddMovieRequest>()
            }
            response {
                HttpStatusCode.OK to {
                    description = "Movie added successfully"
                    body<Int>()
                }
                HttpStatusCode.BadRequest to {
                    description = "Wrong data"
                    body<String>()
                }
            }
        }) {
            if (!authorizationDomain.hasAuthorization(
                    AuthorizationToken(
                        call.request.headers["X-TOKEN"] ?: ""
                    )
                )
            ) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val addMovieRequest = call.receive<AddMovieRequest>()
                val result = cinemaDomain.addMovie(
                    addMovieRequest.name,
                    ImdbId(addMovieRequest.imdbId)
                )

                when (result) {
                    is Either.Right -> {
                        call.respond(HttpStatusCode.OK, result.value.id)
                    }

                    is Either.Left -> {
                        call.respond(HttpStatusCode.BadRequest, result.value)
                    }
                }
            }
        }

        post("/movies/{id}/showtime", {
            description = "Add a showtime for a movie"
            request {
                headerParameter<AuthorizationToken>("X-TOKEN") {
                    description = "Authorization token"
                }
                queryParameter<Int>("id") {
                    description = "Movie identifier"
                }
                body<AddShowTimeRequest>()
            }
            response {
                HttpStatusCode.OK to {
                    description = "Showtime added successfully"
                }
                HttpStatusCode.BadRequest to {
                    description = "Error description"
                    body<String>()
                }
            }
        }) {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid Movie ID"
                )
            val addShowTime = call.receive<AddShowTimeRequest>()
            val date =
                runCatching { LocalDate.parse(addShowTime.date) }.getOrNull()
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid Date"
                    )

            val result = cinemaDomain.addMovieShowTime(
                MovieId(id),
                ShowTime(
                    date,
                    Time(addShowTime.hour, addShowTime.minute)
                )
            )

            when (result) {
                is Either.Right -> {
                    call.respond(HttpStatusCode.OK, result.value)
                }

                is Either.Left -> {
                    call.respond(HttpStatusCode.BadRequest, result.value)
                }
            }
        }

        get("/movies", {
            description = "Fetch movies with pagination"
            request {
                headerParameter<AuthorizationToken>("X-TOKEN") {
                    description = "Authorization token"
                }
                queryParameter<Int>("page") {
                    description = "Page offset"
                }
                queryParameter<Int>("limit") {
                    description = "No of elements"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Movies fetched successfully"
                }
                HttpStatusCode.BadRequest to {
                    description = "Error description"
                    body<String>()
                }
            }
        }) {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
                ?: MAX_LIMIT

            val movies = cinemaDomain.fetchMovies(page, limit)
            call.respond(HttpStatusCode.OK, movies)
        }

        get("/movies/{id}", {
            description = "Fetch details of a specific movie"
            request {
                headerParameter<AuthorizationToken>("X-TOKEN") {
                    description = "Authorization token"
                }
                queryParameter<Int>("id") {
                    description = "Movie identifier"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Movie details"
                    body<Movie>()
                }
                HttpStatusCode.NotFound to {
                    description = "Error description"
                    body<String>()
                }
            }
        }) {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid Movie ID"
                )

            val result = cinemaDomain.fetchMovieDetails(MovieId(id))
            when (result) {
                is Either.Right -> {
                    call.respond(HttpStatusCode.OK, result.value)
                }

                is Either.Left -> {
                    call.respond(HttpStatusCode.NotFound, result.value)
                }
            }
        }

        delete("/movies/{id}", {
            description = "Withdraw a movie from the catalog"
            request {
                headerParameter<AuthorizationToken>("X-TOKEN") {
                    description = "Authorization token"
                }
                queryParameter<Int>("id") {
                    description = "Movie identifier"
                }
            }
            response {
                HttpStatusCode.NoContent to {
                }
                HttpStatusCode.BadRequest to {
                    description = "Error description"
                    body<String>()
                }
            }
        }) {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid Movie ID"
                )

            cinemaDomain.withdrawalMovie(MovieId(id))
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
