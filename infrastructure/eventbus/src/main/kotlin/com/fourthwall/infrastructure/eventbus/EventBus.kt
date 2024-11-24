package com.fourthwall.infrastructure.eventbus

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

typealias Subscriber<T> = (T) -> Unit

interface EventBus {

    fun <T> registerSubscriber(
        topic: String,
        subscriber: Subscriber<T>,
        deserializationStrategy: DeserializationStrategy<T>
    )

    fun <T> publish(
        topic: String,
        payload: T,
        serializationStrategy: SerializationStrategy<T>
    )

    fun startSubscribing()

    fun stopSubscribing()
}