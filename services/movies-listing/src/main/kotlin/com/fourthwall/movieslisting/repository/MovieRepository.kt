package com.fourthwall.movieslisting.repository

import arrow.core.Either
import arrow.core.right
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object MoviesTable : IntIdTable("movies") {
    val imdbId = varchar("imdb_id", 255)
    val externalId = integer("external_id").index()
}

class MoviesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MoviesDAO>(MoviesTable)

    var externalId by MoviesTable.externalId
    var imdbId by MoviesTable.imdbId
}

class MovieRepository(val database: Database) {

    init {
        transaction(database) {
            SchemaUtils.create(MoviesTable)
        }
    }

    fun addMovie(externalId: Int, imdbId: String): Either<String, Int> {
        return transaction(database) {
            MoviesDAO.new {
                this.externalId = externalId
                this.imdbId = imdbId
            }.id.value
        }.right()
    }

}