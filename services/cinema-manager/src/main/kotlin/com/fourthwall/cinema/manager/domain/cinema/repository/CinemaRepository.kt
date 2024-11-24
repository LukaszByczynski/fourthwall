package com.fourthwall.cinema.manager.domain.cinema.repository

import arrow.core.Either
import com.fourthwall.cinema.manager.domain.cinema.Movie
import com.fourthwall.cinema.manager.domain.cinema.MovieId

interface CinemaRepository {

    fun createMovie(name: String, imdbId: String): MovieId

    fun fetchMovie(id: MovieId): Either<String, Movie>

    fun fetchMovies(offset: Int, limit: Int): List<Movie>

    fun updateMovie(movie: Movie): Either<String, Boolean>

    fun deleteMovie(id: MovieId)
}