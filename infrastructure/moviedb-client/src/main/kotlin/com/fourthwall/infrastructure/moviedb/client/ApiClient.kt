package com.fourthwall.infrastructure.moviedb.client

import arrow.core.Either
import arrow.core.raise.effect
import arrow.core.raise.toEither
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.ContentEncoding
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MovieResponse(
    @SerialName(value = "Actors")
    val actors: String,
    @SerialName(value = "Title")
    val title: String,
    @SerialName(value = "Year")
    val year: String,
    @SerialName(value = "Rated")
    val rated: String,
    @SerialName(value = "Released")
    val released: String,
    @SerialName(value = "Genre")
    val genre: String,
    @SerialName(value = "Director")
    val director: String,
    @SerialName(value = "Writer")
    val writer: String,
    @SerialName(value = "Language")
    val language: String,
    @SerialName(value = "Country")
    val country: String,
    @SerialName(value = "imdbRating")
    val imdbRating: String
)

class ApiClient(private val apiKey: String) {

    private val json = Json { ignoreUnknownKeys = true }
    private val apiClient: HttpClient = HttpClient(CIO) {
        install(ContentEncoding) {}
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { request, response ->
                !response.status.isSuccess()
            }
            delayMillis { retry ->
                retry * 500L
            }
        }
    }

    suspend fun movieExists(id: String): Either<String, Boolean> {
        return effect {
            val result = apiClient
                .get("http://www.omdbapi.com") {
                    accept(ContentType.Application.Json)
                    contentType(ContentType.Application.Json)

                    parameter("apikey", apiKey)
                    parameter("i", id)
                }

            when (result.status) {
                HttpStatusCode.OK -> true
                HttpStatusCode.NotFound -> false
                else -> raise("Unknown HTTP status code: ${result.status}")
            }
        }.toEither()
    }

    suspend fun fetchMovieDetails(id: String): Either<String, MovieResponse> {
        return effect<String, MovieResponse> {
            val body = apiClient
                .get("http://www.omdbapi.com") {
                    accept(ContentType.Application.Json)
                    contentType(ContentType.Application.Json)

                    parameter("apikey", apiKey)
                    parameter("i", id)
                }.bodyAsText()
            // Decoding manually due to problems with properly handling
            // content negotiation with the OMDB API service.
            json.decodeFromString(body)
        }.toEither()
    }
}
