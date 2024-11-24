package com.fourthwall.infrastructure.eventbus

import kotlinx.atomicfu.atomic
import kotlinx.serialization.serializer
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractEventBusTest {

    protected abstract var eventBus: EventBus

    protected fun <T> assertEqualsWithTimeout(
        expected: T,
        actualProvider: () -> T,
        timeoutMillis: Long = 200,
        pollingInterval: Long = 100 // Default polling interval
    ) {
        val endTime = System.currentTimeMillis() + timeoutMillis

        while (System.currentTimeMillis() < endTime) {
            val actual = actualProvider()
            if (actual == expected) {
                return
            }
            Thread.sleep(pollingInterval)
        }

        assertEquals(expected, actualProvider())
    }

    @Test
    fun `test register and publish single subscriber`() {
        val noEvents = atomic(0)

        eventBus.startSubscribing()
        eventBus.registerSubscriber<String>(
            "test", { noEvents.addAndGet(1) }, serializer()
        )
        eventBus.publish("test", "test", serializer())

        assertEqualsWithTimeout(1, { noEvents.value })
        eventBus.stopSubscribing()
    }

    @Test
    fun `test register multiple subscribers and publish`() {
        val receivedPayloads =
            Collections.synchronizedList(mutableListOf<String>())
        val subscriber1: Subscriber<String> =
            { payload -> receivedPayloads.add(payload) }
        val subscriber2: Subscriber<String> =
            { payload -> receivedPayloads.add(payload) }

        eventBus.startSubscribing()
        eventBus.registerSubscriber("testTopic", subscriber1, serializer())
        eventBus.registerSubscriber("testTopic", subscriber2, serializer())
        eventBus.publish("testTopic", "Hello, Subscriber!", serializer())

        assertEqualsWithTimeout(2, { receivedPayloads.size })
        assertTrue(receivedPayloads.contains("Hello, Subscriber!"))

        eventBus.stopSubscribing()
    }

    @Test
    fun `test publish to non-existent topic does not crash`() {
        eventBus.publish("nonExistentTopic", "Should not crash", serializer())
        assertTrue(true)
    }

    @Test
    fun `test publish with different types`() {
        var stringReceived: String? = null
        var intReceived: Int? = null

        val stringSubscriber: Subscriber<String> =
            { payload -> stringReceived = payload }
        val intSubscriber: Subscriber<Int> =
            { payload -> intReceived = payload }

        eventBus.startSubscribing()
        eventBus.registerSubscriber(
            "stringTopic", stringSubscriber, serializer()
        )
        eventBus.registerSubscriber("intTopic", intSubscriber, serializer())

        eventBus.publish("stringTopic", "String Event", serializer())
        eventBus.publish("intTopic", 42, serializer())


        assertEqualsWithTimeout("String Event", { stringReceived })
        assertEqualsWithTimeout(42, { intReceived })

        eventBus.stopSubscribing()
    }
}