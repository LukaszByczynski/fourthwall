package com.fourthwall.infrastructure.eventbus.inmem

import com.fourthwall.infrastructure.eventbus.AbstractEventBusTest
import com.fourthwall.infrastructure.eventbus.EventBus
import kotlin.test.BeforeTest

class InMemEventBusTest : AbstractEventBusTest() {

    override lateinit var eventBus: EventBus

    @BeforeTest
    fun setUp() {
        eventBus = InMemEventBus()
    }
}