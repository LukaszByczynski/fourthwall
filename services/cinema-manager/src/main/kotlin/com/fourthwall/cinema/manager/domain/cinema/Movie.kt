package com.fourthwall.cinema.manager.domain.cinema

import arrow.core.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class MovieId(val id: Int) {
    fun validate(): Either<String, Unit> {
        return if (id > 0)
            return right().map { }
        else
            "Invalid ID".left()
    }
}


@Serializable
@JvmInline
value class ImdbId(val id: String) {
    fun validate(): Either<String, Unit> {
        return if (id.startsWith("tt") && id.length == 9)
            return right().map { }
        else
            "Invalid ImdbID".left()
    }
}

@Serializable
data class Time(val hour: Int, val minute: Int) {
    fun validate(): Either<String, Unit> {
        return if (hour < 0 || hour > 23) {
            "Hour for Time must be between 0 and 23".left()
        } else if (minute < 0 || minute > 59) {
            "Minute for Time must be between 0 and 59".left()
        } else right().map { }
    }
}

@Serializable
data class ShowTime(val date: LocalDate, val times: List<Time> = listOf()) {
    fun validate(): Either<String, Unit> {
        return times
            .map { it.validate() }
            .flattenOrAccumulate { acc, s -> acc + "\n" + s }
            .map { }
    }
}

@Serializable
data class Movie(
    val id: MovieId,
    val name: String,
    val imdbId: ImdbId,
    val showTimes: List<ShowTime> = listOf()
) {
    fun validate(): Either<String, Unit> {
        val validationList = listOf(
            id.validate(),
            imdbId.validate()
        ) + showTimes.map { it.validate() }

        return validationList
            .flattenOrAccumulate { acc, s -> acc + "\n" + s }
            .map { }
    }
}
