package com.fourthwall.movieslisting

import arrow.core.flatMap
import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.eventbus.postgres.PostgresEventBus
import com.fourthwall.infrastructure.moviedb.client.ApiClient
import com.fourthwall.movieslisting.api.v1.movieListingRoutes
import com.fourthwall.movieslisting.domain.MovieListingDomain
import com.fourthwall.movieslisting.domain.RefreshMovieDetails
import com.fourthwall.movieslisting.repository.MovieDetailsRepository
import com.fourthwall.movieslisting.repository.MovieRepository
import com.fourthwall.movieslisting.repository.MoviesShowTimesRepository
import com.fourthwall.shared.domain.events.MovieAddedCmd
import com.fourthwall.shared.domain.events.ShowTimeAdded
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun configureApplicationDatabase(config: ApplicationConfig): Database {
    return Database.connect(
        driver = "org.postgresql.Driver",
        url = config.propertyOrNull("databases.movieslistingdb.url")
            ?.getString() ?: "",
        user = config.propertyOrNull("databases.movieslistingdb.user")
            ?.getString() ?: "",
        password = config.propertyOrNull("databases.movieslistingdb.password")
            ?.getString() ?: "",
    )
}

fun configureEventBus(config: ApplicationConfig): EventBus {
    return PostgresEventBus(
        clientId = "movieslisting",
        jdbcUrl = config.propertyOrNull("databases.eventbusdb.url")?.getString()
            ?: "",
        username = config.propertyOrNull("databases.eventbusdb.user")
            ?.getString() ?: "",
        password = config.propertyOrNull("databases.eventbusdb.password")
            ?.getString() ?: ""
    )
}

fun Application.module(
    database: Database = configureApplicationDatabase(environment.config),
    eventBus: EventBus = configureEventBus(environment.config)
) {
    install(SwaggerUI)
    install(ContentNegotiation) {
        json() // Enable JSON serialization
    }
    val listingDomain = MovieListingDomain(
        MovieRepository(database),
        MovieDetailsRepository(database),
        MoviesShowTimesRepository(database),
        eventBus
    )

    val omdbApiClient = ApiClient(
        environment.config.propertyOrNull("apis.omdbapi_key")?.getString() ?: ""
    )

    routing {
        // add the routes for swagger-ui and api-spec
        route("api.json") {
            openApiSpec()
        }
        route("swagger") {
            swaggerUI("/api.json")
        }

        route("api/v1") {
            movieListingRoutes(listingDomain)
        }
    }

    eventBus.registerSubscriber<ShowTimeAdded>(
        "add-movie-show-time", {
            listingDomain.addMovieShowTime(
                it.movie_id,
                it.date,
                it.hour,
                it.minute
            )
        }, serializer()
    )

    eventBus.registerSubscriber<MovieAddedCmd>(
        "add-movie", {
            listingDomain.addNewMovie(it.id, it.title, it.imdbId)
        }, serializer()
    )

    eventBus.registerSubscriber<RefreshMovieDetails>(
        "refresh-movie-details", { event ->
            runBlocking {
                omdbApiClient.fetchMovieDetails(event.imdbId)
            }.flatMap {
                listingDomain.updateMovieDetails(event.id, it)
            }
        }, serializer()
    )

    monitor.subscribe(ApplicationStarted) {
        eventBus.startSubscribing()
    }

    monitor.subscribe(ApplicationStopping) {
        eventBus.stopSubscribing()
    }
}
