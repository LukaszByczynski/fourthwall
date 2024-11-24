package com.fourthwall.cinema.manager.domain.cinema.repository

import arrow.core.Either
import arrow.core.right
import arrow.core.toOption
import com.fourthwall.cinema.manager.domain.cinema.ImdbId
import com.fourthwall.cinema.manager.domain.cinema.Movie
import com.fourthwall.cinema.manager.domain.cinema.MovieId

class InMemoryCinemaRepository : CinemaRepository {
    private val movies = mutableMapOf<MovieId, Movie>()

    override fun createMovie(name: String, imdbId: String): MovieId {
        val movieId = MovieId(movies.size + 1)
        val movie = Movie(movieId, name, ImdbId(imdbId), emptyList())
        movies[movieId] = movie
        return movieId
    }

    override fun fetchMovie(id: MovieId): Either<String, Movie> {
        return movies[id].toOption().toEither { "Movie not found" }
    }

    override fun fetchMovies(page: Int, limit: Int): List<Movie> {
        return movies.values.toList().drop(page * limit).take(limit)
    }

    override fun updateMovie(movie: Movie): Either<String, Boolean> {
        movies[movie.id] = movie
        return true.right()
    }

    override fun deleteMovie(id: MovieId) {
        movies.remove(id)
    }
}