package com.fourthwall.movieslisting.repository

import arrow.core.Either
import arrow.core.right
import com.fourthwall.movieslisting.domain.MovieShowTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.time
import org.jetbrains.exposed.sql.transactions.transaction

object MoviesShowTimesTable : IntIdTable("movie_show_times") {
    val movie_id = integer("movie_id").references(MoviesTable.id)
    val show_date = date("show_date").index()
    val show_time = time("show_time")
}

class MoviesShowTimesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MoviesShowTimesDAO>(MoviesShowTimesTable)

    var movieId by MoviesShowTimesTable.movie_id
    var showDate by MoviesShowTimesTable.show_date
    var showTime by MoviesShowTimesTable.show_time
}

class MoviesShowTimesRepository(val database: Database) {

    init {
        transaction(database) {
            SchemaUtils.create(MoviesShowTimesTable)
        }
    }

    fun addShowTime(
        movieId: Int,
        date: LocalDate,
        hour: Int,
        minute: Int
    ): Either<String, Int> {
        return transaction(database) {
            MoviesShowTimesDAO.new {
                this.movieId = movieId
                this.showDate = date
                this.showTime = LocalTime(hour, minute)
            }.id.value
        }.right()
    }

    fun fetchShowTimes(id: Int): Either<String, List<MovieShowTime>> {
        return transaction(database) {
            MoviesShowTimesDAO
                .find({ MoviesShowTimesTable.movie_id eq id })
                .map {
                    MovieShowTime(
                        it.showDate,
                        it.showTime.hour,
                        it.showTime.minute
                    )
                }.right()
        }
    }

}