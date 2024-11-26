package com.fourthwall.cinema.manager

import com.fourthwall.cinema.manager.api.v1.AddMovieRequest
import com.fourthwall.cinema.manager.api.v1.AddShowTimeRequest
import com.fourthwall.cinema.manager.api.v1.LoginRequest
import com.fourthwall.cinema.manager.domain.cinema.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.*

class CinemaRoutesE2ETest {

    private val testToken = "test-token"
    private val postgresContainer: PostgreSQLContainer<Nothing>

    init {
        postgresContainer = PostgreSQLContainer<Nothing>(
            DockerImageName.parse("postgres:16-alpine")
        ).waitingFor(
            Wait.defaultWaitStrategy()
        )

        postgresContainer.start()
    }

    @BeforeTest
    fun setUp() {
        Database.connect(
            postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )
    }

    @Test
    fun `Scenario 1 Add a Movie and Fetch Its Details`() = testApplication {
        application {
            module()
        }

        runBlocking {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            // Step 1: Authorize
            val token: String = client.post("/api/v1/authorize/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        "admin",
                        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
                    )
                )
            }.body()

            // Step 2: Add a movie
            val addMovieResponse: MovieId =
                client.post("/api/v1/cinema/movies/add") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddMovieRequest("Inception", "tt1375666"))
                }.body()

            assertNotNull(addMovieResponse.id)

            // Step 3: Fetch the movie details
            val fetchResponse: Movie = client
                .get("/api/v1/cinema/movies/${addMovieResponse.id}") {
                    header("X-TOKEN", token)
                }
                .body()

            assertEquals("Inception", fetchResponse.name)
            assertEquals(ImdbId("tt1375666"), fetchResponse.imdbId)
        }
    }

    @Test
    fun `Scenario 2 Add a ShowTime for a Movie`() = testApplication {
        application {
            module()
        }

        runBlocking {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            // Step 1: Authorize
            val token: String = client.post("/api/v1/authorize/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        "admin",
                        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
                    )
                )
            }.body()

            // Step 2: Add a movie
            val addMovieResponse: MovieId =
                client.post("/api/v1/cinema/movies/add") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddMovieRequest("Inception", "tt1375666"))
                }.body()

            assertNotNull(addMovieResponse.id)

            // Step 3. Add a showtime for the movie
            val addShowTimeResponse =
                client.post("/api/v1/cinema/movies/${addMovieResponse.id}/showtime") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddShowTimeRequest("2024-12-01", 14, 30))
                }

            assertEquals(HttpStatusCode.OK, addShowTimeResponse.status)

            // Step 4: Fetch the movie details
            val fetchResponse: Movie = client
                .get("/api/v1/cinema/movies/${addMovieResponse.id}") {
                    header("X-TOKEN", token)
                }
                .body()

            assertEquals("Inception", fetchResponse.name)
            assertEquals(ImdbId("tt1375666"), fetchResponse.imdbId)
            assertContains(
                fetchResponse.showTimes,
                ShowTime(LocalDate.parse("2024-12-01"), listOf(Time(14, 30)))
            )
        }
    }


    @Test
    fun `Scenario 3 Fetch All Movies with Pagination`() = testApplication {
        application {
            module()
        }

        runBlocking {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            // Step 1: Authorize
            val token: String = client.post("/api/v1/authorize/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        "admin",
                        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
                    )
                )
            }.body()

            // Step 2: Add movies
            val addMovieResponse1: MovieId =
                client.post("/api/v1/cinema/movies/add") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddMovieRequest("Inception", "tt1375666"))
                }.body()

            assertNotNull(addMovieResponse1.id)

            val addMovieResponse2: MovieId =
                client.post("/api/v1/cinema/movies/add") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddMovieRequest("Paw Patrol", "tt1375667"))
                }.body()

            assertNotNull(addMovieResponse2.id)

            // Fetch movies
            val response: List<Movie> =
                client.get("/api/v1/cinema/movies?page=0&limit=1") {
                    header("X-TOKEN", testToken)
                }.body()

            assertTrue(response.isNotEmpty())
        }
    }

    @Test
    fun `Scenario 4 Delete a Movie`() = testApplication {
        application {
            module()
        }

        runBlocking {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            // Step 1: Authorize
            val token: String = client.post("/api/v1/authorize/login") {
                contentType(ContentType.Application.Json)
                setBody(
                    LoginRequest(
                        "admin",
                        "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"
                    )
                )
            }.body()

            // Step 2: Add a movie
            val addMovieResponse: MovieId =
                client.post("/api/v1/cinema/movies/add") {
                    header("X-TOKEN", token)
                    contentType(ContentType.Application.Json)
                    setBody(AddMovieRequest("Inception", "tt1375666"))
                }.body()

            assertNotNull(addMovieResponse.id)

            // 3. Delete the movie
            val deleteResponseStatus =
                client.delete("/api/v1/cinema/movies/${addMovieResponse.id}") {
                    header("X-TOKEN", token)
                }.status

            assertEquals(HttpStatusCode.NoContent, deleteResponseStatus)

            // Verify the movie is deleted
            val fetchResponseStatus = client
                .get("/api/v1/cinema/movies/${addMovieResponse.id}") {
                    header("X-TOKEN", token)
                }.status

            assertEquals(HttpStatusCode.NotFound, fetchResponseStatus)
        }
    }


// @Test
// fun `Scenario 5 Handle Invalid Movie ID`() = testApplication {
//  application {
//   module() // Replace with your actual module function
//  }
//
//  val response = client.get<HttpResponse>("/cinema/movies/invalid_id") {
//   addHeader("X-TOKEN", testToken)
//  }
//
//  assertEquals(HttpStatusCode.BadRequest, response.status)
// }
}
