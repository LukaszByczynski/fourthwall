package com.fourthwall.cinema.manager.domain.cinema

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.fourthwall.cinema.manager.domain.cinema.repository.CinemaRepository
import org.jetbrains.exposed.sql.transactions.transaction

const val MAX_LIMIT = 10

class CinemaDomain(
    val cinemaRepository: CinemaRepository
) {

    fun addMovie(name: String, imdbId: ImdbId): Either<String, MovieId> {
        return imdbId.validate().map {
            cinemaRepository.createMovie(name, imdbId.id)
        }
    }

    fun addMovieShowTime(
        id: MovieId,
        showTime: ShowTime
    ): Either<String, Unit> {
        return transaction {
            fetchMovieDetails(id).flatMap {
                if (!it.showTimes.contains(showTime)) {
                    cinemaRepository.updateMovie(
                        it.copy(
                            showTimes = it.showTimes + showTime
                        )
                    )
                } else it.right()
            }.map {}
        }
    }

    fun fetchMovies(page: Int, limit: Int): List<Movie> {
        return cinemaRepository.fetchMovies(
            Math.max(page, 0),
            Math.min(MAX_LIMIT, limit)
        )
    }

    fun fetchMovieDetails(id: MovieId): Either<String, Movie> {
        return cinemaRepository.fetchMovie(id)
    }

    fun withdrawalMovie(id: MovieId) {
        cinemaRepository.deleteMovie(id)
    }
}