package com.fourthwall.infrastructure.eventbus.postgres

import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.eventbus.Subscriber
import com.fourthwall.infrastructure.eventbus.postgres.repository.ClientOffsetRepository
import com.fourthwall.infrastructure.eventbus.postgres.repository.EventBusEvent
import com.fourthwall.infrastructure.eventbus.postgres.repository.EventsRepository
import kotlinx.coroutines.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PostgresEventBus(
    val clientId: String,
    jdbcUrl: String,
    username: String,
    password: String,
    val pollingInterval: Duration = 1.seconds
) : EventBus {
    private var subscribersMap =
        mapOf<String, List<Subscriber<EventBusEvent>>>()
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }
    private val database = Database.connect(
        jdbcUrl,
        driver = "org.postgresql.Driver",
        user = username,
        password = password
    )
    private val clientOffsetRepository = ClientOffsetRepository(database)
    private val eventsRepository = EventsRepository(database)

    override fun startSubscribing() {
        var offset = clientOffsetRepository.fetchOffset(clientId)
        job = scope.launch {
            while (isActive) {
                transaction(database) {
                    val events = eventsRepository.fetchFromOffset(offset)
                    if (events.size > 0) {
                        offset = events.maxOf { it.id }

                        events.forEach { data ->
                            subscribersMap.getOrDefault(
                                data.topic, mutableListOf()
                            ).forEach {
                                it.invoke(data)
                            }
                        }

                        clientOffsetRepository.updateOffset(clientId, offset)
                    }
                }
                delay(pollingInterval)
            }
        }
    }

    override fun stopSubscribing() = runBlocking {
        // Cancel the coroutine
        job?.cancel()
        delay(pollingInterval)
        job?.join()
        job = null
    }

    override fun <T> registerSubscriber(
        topic: String,
        subscriber: Subscriber<T>,
        deserializationStrategy: DeserializationStrategy<T>
    ) {
        val handler: Subscriber<EventBusEvent> = {
            val data =
                json.decodeFromString(deserializationStrategy, it.payload)
            subscriber.invoke(data)
        }

        subscribersMap = subscribersMap.toMutableMap().apply {
            put(topic, (this[topic] ?: emptyList()) + handler)
        }
    }

    override fun <T> publish(
        topic: String,
        payload: T,
        serializationStrategy: SerializationStrategy<T>
    ) {
        transaction(database) {
            eventsRepository.publish(
                topic, json.encodeToString(serializationStrategy, payload)
            )
        }
    }
}