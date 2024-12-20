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


import kotlinx.serialization.Serializable as KSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import java.io.Serializable

/**
 * 
 *
 * @param poster 
 * @param title 
 * @param type 
 * @param year 
 * @param imdbID 
 */
@KSerializable

data class CombinedResultSearchInner (

    @SerialName(value = "Poster")
    val poster: kotlin.String,

    @SerialName(value = "Title")
    val title: kotlin.String,

    @SerialName(value = "Type")
    val type: kotlin.String,

    @SerialName(value = "Year")
    val year: kotlin.String,

    @SerialName(value = "imdbID")
    val imdbID: kotlin.String

) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }


}

