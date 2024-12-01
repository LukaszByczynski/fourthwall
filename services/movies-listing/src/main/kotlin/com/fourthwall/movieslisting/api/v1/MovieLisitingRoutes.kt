package com.fourthwall.movieslisting.api.v1

import arrow.core.Either
import com.fourthwall.movieslisting.domain.Movie
import com.fourthwall.movieslisting.domain.MovieDetails
import com.fourthwall.movieslisting.domain.MovieListingDomain
import com.fourthwall.movieslisting.domain.MovieShowTime
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.movieListingRoutes(
    listingDomain: MovieListingDomain
) {
    route("lisiting") {
        get("/movies", {
            description = "Adds a new movie to the cinema"
            response {
                HttpStatusCode.OK to {
                    description = "Movies list"
                    body<List<Movie>>()
                }
                HttpStatusCode.BadRequest to {
                    description = "Error description"
                    body<String>()
                }
            }
        }) {
            val result = listingDomain.fetchMovies()
            when (result) {
                is Either.Right -> {
                    call.respond(HttpStatusCode.OK, result.value)
                }

                is Either.Left -> {
                    call.respond(HttpStatusCode.BadRequest, "Subsystem error")
                }
            }
        }

        get("/movies/{id}", {
            description = "Fetch details of a specific movie"
            request {
                queryParameter<Int>("id") {
                    description = "Movie identifier"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Movie details"
                    body<MovieDetails>()
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

            val result = listingDomain.fetchMovieDetails(id)
            when (result) {
                is Either.Right -> {
                    call.respond(HttpStatusCode.OK, result.value)
                }

                is Either.Left -> {
                    call.respond(HttpStatusCode.NotFound, result.value)
                }
            }
        }

        get("/movies/{id}/showtimes", {
            description = "Fetch details of a specific movie"
            request {
                queryParameter<Int>("id") {
                    description = "Movie identifier"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Movie show times"
                    body<MovieShowTime>()
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

            val result = listingDomain.fetchMovieShowTimes(id)
            when (result) {
                is Either.Right -> {
                    call.respond(HttpStatusCode.OK, result.value)
                }

                is Either.Left -> {
                    call.respond(HttpStatusCode.NotFound, result.value)
                }
            }
        }
    }
}