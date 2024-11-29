package com.fourthwall.shared.domain.events

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MovieAddedCmd(val id: Int, val title: String, val imdbId: String)

@Serializable
data class ShowTimeAdded(
    val movie_id: Int,
    val date: LocalDate,
    val hour: Int,
    val minute: Int
)