package com.fourthwall.movieslisting.repository

import arrow.core.Either
import arrow.core.firstOrNone
import arrow.core.right
import arrow.core.toOption
import com.fourthwall.infrastructure.eventbus.postgres.repository.ClientOffsetRepository.ClientOffset.references
import com.fourthwall.movieslisting.domain.MovieDetails
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object MovieDetailsTable : IntIdTable("movie_details") {
    val movie_id = integer("movie_id").references(MoviesTable.id)
    val actors = varchar("actors", 255).default("")
    val title = varchar("title", 255).default("")
    val year = varchar("year", 255).default("")
    val rated = varchar("rated", 255).default("")
    val released = varchar("released", 255).default("")
    val genre = varchar("genre", 255).default("")
    val director = varchar("director", 255).default("")
    val writer = varchar("writer", 255).default("")
    val language = varchar("language", 255).default("")
    val country = varchar("country", 255).default("")
    val imdbRating = varchar("imdbRating", 255).default("")
}

class MovieDetailsDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDetailsDAO>(MovieDetailsTable)

    var movie_id by MovieDetailsTable.movie_id.references(MoviesTable.id)
    var actors by MovieDetailsTable.actors
    var title by MovieDetailsTable.title
    var year by MovieDetailsTable.year
    var rated by MovieDetailsTable.rated
    var released by MovieDetailsTable.released
    var genre by MovieDetailsTable.genre
    var director by MovieDetailsTable.director
    var writer by MovieDetailsTable.writer
    var language by MovieDetailsTable.language
    var country by MovieDetailsTable.country
    var imdbRating by MovieDetailsTable.imdbRating
}

class MovieDetailsRepository(val database: Database) {

    fun addMovieDetails(id: Int, title: String): Either<String, Unit> {
        return transaction(database) {
            MovieDetailsDAO.new {
                this.movie_id = id
                this.title = title
            }
        }.right().map { }
    }

    init {
        transaction(database) {
            SchemaUtils.create(MovieDetailsTable)
        }
    }

    fun updateMovieDetails(details: MovieDetails): Either<String, Unit> {
        return transaction(database) {
            MovieDetailsDAO
                .findSingleByAndUpdate(
                    MovieDetailsTable.movie_id eq details.id,
                    {
                        it.actors = details.actors
                        it.title = details.title
                        it.year = details.year
                        it.country = details.country
                        it.language = details.language
                        it.imdbRating = details.imdbRating
                        it.rated = details.rated
                        it.released = details.released
                        it.writer = details.writer
                        it.director = details.director
                        it.genre = details.genre
                    }
                ).toOption().toEither { "Moview not found" }.map { }
        }
    }

    fun fetchMovieDetails(id: Int): Either<String, MovieDetails> {
        return transaction(database) {
            MovieDetailsDAO
                .find({ MovieDetailsTable.movie_id eq id })
                .firstOrNone()
                .map {
                    MovieDetails(
                        id = it.movie_id,
                        actors = it.actors,
                        title = it.title,
                        year = it.year,
                        rated = it.rated,
                        released = it.released,
                        genre = it.genre,
                        director = it.director,
                        writer = it.writer,
                        language = it.language,
                        country = it.country,
                        imdbRating = it.imdbRating
                    )
                }.toEither { "Moview not found" }
        }
    }
}