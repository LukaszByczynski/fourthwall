package com.fourthwall.cinema.manager.infrastructure.repository

import com.fourthwall.cinema.manager.domain.cinema.ImdbId
import com.fourthwall.cinema.manager.domain.cinema.Movie
import com.fourthwall.cinema.manager.domain.cinema.MovieId
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.*

class PostgresCinemaDomainRepositoryTest {

    private lateinit var database: Database
    private lateinit var repository: PostgresCinemaRepository

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
        database = Database.connect(
            postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )

        repository = PostgresCinemaRepository()
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(PostgresCinemaRepository.MovieTable)
        }
    }

    @Test
    fun `test create movie`() {
        val movieId = repository.createMovie("Inception", "tt1375666")
        assertTrue(movieId.id > 0)
    }

    @Test
    fun `test fetch movie by id`() {
        val movieId = repository.createMovie("Inception", "tt1375666")
        val fetchedMovie = repository.fetchMovie(movieId)

        assertTrue(fetchedMovie.isRight())
        assertEquals("Inception", fetchedMovie.getOrNull()?.name)
    }

    @Test
    fun `test fetch movie not found`() {
        val result =
            repository.fetchMovie(MovieId(999)) // Assuming this ID does not exist
        assertTrue(result.isLeft())
        assertEquals("Movie not found", result.leftOrNull())
    }

    @Test
    fun `test update movie`() {
        val movieId = repository.createMovie("Inception", "tt1375666")
        val updatedMovie =
            Movie(MovieId(movieId.id), "Inception Updated", ImdbId("tt1375666"))

        val updateResult = repository.updateMovie(updatedMovie)
        assertTrue(updateResult.isRight())
        assertTrue(updateResult.getOrNull() ?: false)

        val fetchedMovie = repository.fetchMovie(movieId)
        assertEquals("Inception Updated", fetchedMovie.getOrNull()?.name)
    }

    @Test
    fun `test delete movie`() {
        val movieId = repository.createMovie("Inception", "tt1375666")
        repository.deleteMovie(movieId)

        val result = repository.fetchMovie(movieId)
        assertTrue(result.isLeft())
        assertEquals("Movie not found", result.swap().getOrNull())
    }

    @Test
    fun `test fetch movies with limit and offset`() {
        repository.createMovie("Movie 1", "tt000001")
        repository.createMovie("Movie 2", "tt000002")
        repository.createMovie("Movie 3", "tt000003")

        val movies = repository.fetchMovies(0, 2)
        assertEquals(2, movies.size)
        assertEquals("Movie 1", movies[0].name)
        assertEquals("Movie 2", movies[1].name)
    }
}