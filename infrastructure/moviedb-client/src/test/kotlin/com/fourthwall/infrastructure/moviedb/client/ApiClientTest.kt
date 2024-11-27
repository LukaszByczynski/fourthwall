package com.fourthwall.infrastructure.moviedb.client

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiClientTest {

    private val apiKey = System.getenv().getOrDefault("OMDBAPI_KEY", "")

    @Test
    fun `Should return MovieResponse`() {
        val apiClientTest = ApiClient(apiKey)

        runBlocking {
            val result = apiClientTest.fetchMovieDetails("tt0232500")

            assertEquals(
                MovieResponse(
                    actors = "Vin Diesel, Paul Walker, Michelle Rodriguez",
                    title = "The Fast and the Furious",
                    year = "2001",
                    rated = "PG-13",
                    released = "22 Jun 2001",
                    genre = "Action, Crime, Thriller",
                    director = "Rob Cohen",
                    writer = "Ken Li, Gary Scott Thompson, Erik Bergquist",
                    language = "English, Spanish",
                    country = "United States, Germany",
                    imdbRating = "6.8"
                ),
                result.getOrNull()
            )
        }
    }

    @Test
    fun `Should return true if movie exists`() {
        val apiClientTest = ApiClient(apiKey)

        runBlocking {
            val result = apiClientTest.movieExists("tt0232500")

            assertEquals(true, result.getOrNull())
        }
    }

    @Test
    fun `Should return false if movie not exists`() {
        val apiClientTest = ApiClient(apiKey)

        runBlocking {
            val result = apiClientTest.movieExists("ttxxxxxxx")

            assertEquals(true, result.getOrNull())
        }
    }

    @Test
    fun `Should return error`() {
        val apiClientTest = ApiClient("")

        runBlocking {
            val result = apiClientTest.movieExists("ttxxxxxxx")

            assertEquals(true, result.isLeft())
            assertEquals(
                "Unknown HTTP status code: 401 Unauthorized",
                result.leftOrNull()
            )
        }
    }

}