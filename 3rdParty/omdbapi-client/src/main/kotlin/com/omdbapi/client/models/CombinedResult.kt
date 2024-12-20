/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.omdbapi.client.models

import com.omdbapi.client.models.CombinedResultSearchInner

import kotlinx.serialization.Serializable as KSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.io.Serializable

/**
 * 
 *
 * @param response 
 * @param actors 
 * @param awards 
 * @param boxOffice 
 * @param country 
 * @param DVD 
 * @param director 
 * @param genre 
 * @param language 
 * @param metascore 
 * @param plot 
 * @param poster 
 * @param production 
 * @param rated 
 * @param released 
 * @param runtime 
 * @param search 
 * @param title 
 * @param type 
 * @param website 
 * @param writer 
 * @param year 
 * @param imdbID 
 * @param imdbRating 
 * @param imdbVotes 
 * @param tomatoConsensus 
 * @param tomatoFresh 
 * @param tomatoImage 
 * @param tomatoMeter 
 * @param tomatoRating 
 * @param tomatoReviews 
 * @param tomatoRotten 
 * @param tomatoURL 
 * @param tomatoUserMeter 
 * @param tomatoUserRating 
 * @param tomatoUserReviews 
 * @param totalResults 
 * @param totalSeasons 
 */
@KSerializable

data class CombinedResult (

    @SerialName(value = "Response")
    val response: kotlin.String,

    @SerialName(value = "Actors")
    val actors: kotlin.String? = null,

    @SerialName(value = "Awards")
    val awards: kotlin.String? = null,

    @SerialName(value = "BoxOffice")
    val boxOffice: kotlin.String? = null,

    @SerialName(value = "Country")
    val country: kotlin.String? = null,

    @SerialName(value = "DVD")
    val DVD: kotlin.String? = null,

    @SerialName(value = "Director")
    val director: kotlin.String? = null,

    @SerialName(value = "Genre")
    val genre: kotlin.String? = null,

    @SerialName(value = "Language")
    val language: kotlin.String? = null,

    @SerialName(value = "Metascore")
    val metascore: kotlin.String? = null,

    @SerialName(value = "Plot")
    val plot: kotlin.String? = null,

    @SerialName(value = "Poster")
    val poster: kotlin.String? = null,

    @SerialName(value = "Production")
    val production: kotlin.String? = null,

    @SerialName(value = "Rated")
    val rated: kotlin.String? = null,

    @SerialName(value = "Released")
    val released: kotlin.String? = null,

    @SerialName(value = "Runtime")
    val runtime: kotlin.String? = null,

    @SerialName(value = "Search")
    val search: kotlin.collections.List<CombinedResultSearchInner>? = null,

    @SerialName(value = "Title")
    val title: kotlin.String? = null,

    @SerialName(value = "Type")
    val type: kotlin.String? = null,

    @SerialName(value = "Website")
    val website: kotlin.String? = null,

    @SerialName(value = "Writer")
    val writer: kotlin.String? = null,

    @SerialName(value = "Year")
    val year: kotlin.String? = null,

    @SerialName(value = "imdbID")
    val imdbID: kotlin.String? = null,

    @SerialName(value = "imdbRating")
    val imdbRating: kotlin.String? = null,

    @SerialName(value = "imdbVotes")
    val imdbVotes: kotlin.String? = null,

    @SerialName(value = "tomatoConsensus")
    val tomatoConsensus: kotlin.String? = null,

    @SerialName(value = "tomatoFresh")
    val tomatoFresh: kotlin.String? = null,

    @SerialName(value = "tomatoImage")
    val tomatoImage: kotlin.String? = null,

    @SerialName(value = "tomatoMeter")
    val tomatoMeter: kotlin.String? = null,

    @SerialName(value = "tomatoRating")
    val tomatoRating: kotlin.String? = null,

    @SerialName(value = "tomatoReviews")
    val tomatoReviews: kotlin.String? = null,

    @SerialName(value = "tomatoRotten")
    val tomatoRotten: kotlin.String? = null,

    @SerialName(value = "tomatoURL")
    val tomatoURL: kotlin.String? = null,

    @SerialName(value = "tomatoUserMeter")
    val tomatoUserMeter: kotlin.String? = null,

    @SerialName(value = "tomatoUserRating")
    val tomatoUserRating: kotlin.String? = null,

    @SerialName(value = "tomatoUserReviews")
    val tomatoUserReviews: kotlin.String? = null,

    @SerialName(value = "totalResults")
    val totalResults: kotlin.String? = null,

    @SerialName(value = "totalSeasons")
    val totalSeasons: kotlin.String? = null

) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }


}

