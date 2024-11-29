package com.fourthwall.movieslisting.domain

import arrow.core.Either
import arrow.core.flatMap
import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.moviedb.client.MovieResponse
import com.fourthwall.movieslisting.repository.MovieDetailsRepository
import com.fourthwall.movieslisting.repository.MovieRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable
data class RefreshMovieDetails(val id: Int, val imdbId: String)

@Serializable
data class MovieDetails(
    val movie_id: Int,
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

class MovieListingDomain(
    val movieRepository: MovieRepository,
    val movieDetailsRepository: MovieDetailsRepository,
    val eventBus: EventBus
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
            movieDetailsRepository.addMovieDetails(id).map { id }
        }
    }

    fun updateMovieDetails(
        movieId: Int,
        details: MovieResponse
    ): Either<String, Unit> {
        return movieDetailsRepository.updateMovieDetails(
            MovieDetails(
                movie_id = movieId,
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
}