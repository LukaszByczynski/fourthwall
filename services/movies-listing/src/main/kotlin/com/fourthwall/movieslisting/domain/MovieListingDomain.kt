package com.fourthwall.movieslisting.domain

import arrow.core.Either
import arrow.core.flatMap
import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.moviedb.client.MovieResponse
import com.fourthwall.movieslisting.repository.MovieDetailsRepository
import com.fourthwall.movieslisting.repository.MovieRepository
import com.fourthwall.movieslisting.repository.MoviesShowTimesRepository
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
data class RefreshMovieDetails(val id: Int, val imdbId: String)

@Serializable
data class MovieDetails(
    val id: Int,
    val actors: String = "",
    val title: String = "",
    val year: String = "",
    val rated: String = "",
    val released: String = "",
    val genre: String = "",
    val director: String = "",
    val writer: String = "",
    val language: String = "",
    val country: String = "",
    val imdbRating: String = ""
)

@Serializable
data class Movie(val id: Int, val title: String)

@Serializable
data class MovieShowTime(val date: LocalDate, val hour: Int, val minute: Int)

class MovieListingDomain(
    private val movieRepository: MovieRepository,
    private val movieDetailsRepository: MovieDetailsRepository,
    private val moviesShowTimesRepository: MoviesShowTimesRepository,
    private val eventBus: EventBus
) {

    fun addNewMovie(
        externalId: Int,
        title: String,
        imdbId: String
    ): Either<String, Int> {
        return movieRepository.addMovie(externalId, imdbId).flatMap { id ->
            eventBus.publish(
                "refresh-movie-details",
                RefreshMovieDetails(id, imdbId),
                serializer()
            )
            movieDetailsRepository.addMovieDetails(id, title).map { id }
        }
    }

    fun updateMovieDetails(
        movieId: Int,
        details: MovieResponse
    ): Either<String, Unit> {
        return movieDetailsRepository.updateMovieDetails(
            MovieDetails(
                id = movieId,
                actors = details.actors,
                title = details.title,
                year = details.year,
                country = details.country,
                language = details.language,
                imdbRating = details.imdbRating,
                rated = details.rated,
                released = details.released,
                writer = details.writer,
                director = details.director,
                genre = details.genre
            )
        )
    }

    fun addMovieShowTime(
        movieId: Int,
        date: LocalDate,
        hour: Int,
        minute: Int
    ): Either<String, Int> {
        return moviesShowTimesRepository.addShowTime(
            movieId,
            date,
            hour,
            minute
        )
    }

    fun fetchMovies(): Either<Nothing, List<Movie>> {
        return movieRepository.fetchMovies().map {
            it.map {
                Movie(it.id, it.title)
            }
        }
    }

    fun fetchMovieDetails(id: Int): Either<String, MovieDetails> {
        return movieDetailsRepository.fetchMovieDetails(id)
    }

    fun fetchMovieShowTimes(id: Int): Either<String, List<MovieShowTime>> {
        return moviesShowTimesRepository.fetchShowTimes(id)
    }
}