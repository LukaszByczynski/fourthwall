package com.fourthwall.infrastructure.eventbus.inmem

import com.fourthwall.infrastructure.eventbus.EventBus
import com.fourthwall.infrastructure.eventbus.Subscriber
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class InMemEventBus : EventBus {

    private var subscribersMap = mapOf<String, List<Subscriber<String>>>()
    private val json = Json { ignoreUnknownKeys = true }

    override fun <T> registerSubscriber(
        topic: String,
        subscriber: Subscriber<T>,
        deserializationStrategy: DeserializationStrategy<T>
    ) {
        val handler: Subscriber<String> = {
            subscriber.invoke(
                json.decodeFromString(
                    deserializationStrategy,
                    it
                )
            )
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
        subscribersMap
            .getOrDefault(topic, mutableListOf())
            .forEach {
                it.invoke(
                    json.encodeToString(
                        serializationStrategy,
                        payload
                    )
                )
            }
    }

    override fun startSubscribing() {
    }

    override fun stopSubscribing() {
    }
}