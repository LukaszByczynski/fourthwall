package com.fourthwall.shared.domain.events

import kotlinx.serialization.Serializable

@Serializable
data class MovieAddedCmd(val id: Int, val title: String, val imdbId: String)