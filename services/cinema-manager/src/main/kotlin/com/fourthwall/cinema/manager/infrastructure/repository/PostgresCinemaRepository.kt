package com.fourthwall.cinema.manager.infrastructure.repository

import arrow.core.Either
import arrow.core.toOption
import com.fourthwall.cinema.manager.domain.cinema.ImdbId
import com.fourthwall.cinema.manager.domain.cinema.Movie
import com.fourthwall.cinema.manager.domain.cinema.MovieId
import com.fourthwall.cinema.manager.domain.cinema.repository.CinemaRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresCinemaRepository : CinemaRepository {

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        private fun toMovie(it: MovieDAO): Movie {
            val movie: Movie = json.decodeFromString(serializer(), it.payload)
            return movie.copy(id = MovieId(it.id.value))
        }
    }

    init {
        transaction {
            SchemaUtils.create(PostgresCinemaRepository.MovieTable)
        }
    }

    object MovieTable : IntIdTable("movie") {
        val payload = text("payload")
    }

    class MovieDAO(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<MovieDAO>(MovieTable)

        var payload by MovieTable.payload
    }

    override fun createMovie(name: String, imdbId: String): MovieId {
        return transaction {
            val result = MovieDAO.new {
                payload =
                    json.encodeToString(
                        serializer(),
                        Movie(MovieId(-1), name, ImdbId(imdbId))
                    )
            }

            MovieId(result.id.value)
        }
    }

    override fun fetchMovie(id: MovieId): Either<String, Movie> {
        return transaction {
            MovieDAO
                .findById(id.id)
                .toOption()
                .map(::toMovie)
                .toEither { "Movie not found" }
        }
    }

    override fun fetchMovies(offset: Int, limit: Int): List<Movie> {
        return transaction {
            MovieDAO
                .all()
                .offset(offset.toLong())
                .limit(limit)
                .map(::toMovie)
                .toList()
        }
    }

    override fun updateMovie(movie: Movie): Either<String, Boolean> {
        return transaction {
            MovieDAO
                .findById(movie.id.id)
                .toOption()
                .map {
                    it.payload = json.encodeToString(serializer(), movie)
                    it.flush()
                }
                .toEither { "Movie not found" }
        }
    }

    override fun deleteMovie(id: MovieId) {
        transaction { MovieDAO.findById(id.id)?.delete() }
    }
}