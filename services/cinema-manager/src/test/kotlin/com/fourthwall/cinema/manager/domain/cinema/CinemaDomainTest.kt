package com.fourthwall.cinema.manager.domain.cinema

import com.fourthwall.cinema.manager.domain.cinema.repository.InMemoryCinemaRepository
import com.fourthwall.infrastructure.eventbus.inmem.InMemEventBus
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Database
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CinemaDomainTest {

    private val cinemaRepository = InMemoryCinemaRepository()
    private val cinemaDomain = CinemaDomain(cinemaRepository, InMemEventBus())

    @Test
    fun `addMovie should return MovieId when valid ImdbId is provided`() {
        val movieName = "Inception"
        val validImdbId = ImdbId("tt1375666")

        val result = cinemaDomain.addMovie(movieName, validImdbId)

        assertTrue(result.isRight())
        val movieId = result.getOrNull()
        assertEquals(
            1,
            movieId?.id?.toInt()
        ) // Expecting the first movie to have ID 1
    }

    @Test
    fun `addMovie should return error when invalid ImdbId is provided`() {
        val movieName = "Inception"
        val invalidImdbId = ImdbId("invalid_id")

        val result = cinemaDomain.addMovie(movieName, invalidImdbId)

        assertTrue(result.isLeft())
    }

    @Test
    fun `addMovieShowTime should add show time to movie when it does exist`() {
        val movieId = MovieId(1)
        val showTime = ShowTime(LocalDate.parse("2024-01-01"))
        cinemaDomain.addMovie(
            "Inception",
            ImdbId("tt1375666")
        ) // Add movie first

        val result = cinemaDomain.addMovieShowTime(movieId, showTime)

        assertTrue(result.isRight())
        val movieDetails = cinemaDomain.fetchMovieDetails(movieId).getOrNull()
        assertTrue(movieDetails?.showTimes?.contains(showTime) == true)
    }

    @Test
    fun `addMovieShowTime should not add show time if it already exists`() {
        val movieId = MovieId(1)
        val showTime =
            ShowTime(LocalDate.parse("2024-01-01"), listOf(Time(19, 0)))
        cinemaDomain.addMovie(
            "Inception",
            ImdbId("tt1375666")
        ) // Add movie first
        cinemaDomain.addMovieShowTime(movieId, showTime) // Add showtime first

        val result = cinemaDomain.addMovieShowTime(movieId, showTime)

        assertTrue(result.isRight())
        // Ensure the update method is not called
        val movieDetails = cinemaDomain.fetchMovieDetails(movieId).getOrNull()
        assertEquals(
            1,
            movieDetails?.showTimes?.size
        ) // Should still have only one showtime
    }

    @Test
    fun `fetchMovies should return a list of movies`() {
        cinemaDomain.addMovie("Inception", ImdbId("tt1375666"))
        cinemaDomain.addMovie("Interstellar", ImdbId("tt0816692"))

        val result = cinemaDomain.fetchMovies(0, 5)

        assertEquals(2, result.size)
    }

    @Test
    fun `fetchMovieDetails should return movie details`() {
        val movieId = MovieId(1)
        cinemaDomain.addMovie("Inception", ImdbId("tt1375666"))

        val result = cinemaDomain.fetchMovieDetails(movieId)

        assertTrue(result.isRight())
        assertEquals("Inception", result.getOrNull()?.name)
    }

    @Test
    fun `withdrawalMovie should delete movie by id`() {
        val movieId = MovieId(1)
        cinemaDomain.addMovie("Inception", ImdbId("tt1375666"))

        cinemaDomain.withdrawalMovie(movieId)

        val result = cinemaDomain.fetchMovieDetails(movieId)
        assertTrue(result.isLeft())
    }
}