package com.fourthwall.infrastructure.eventbus.postgres

import com.fourthwall.infrastructure.eventbus.AbstractEventBusTest
import com.fourthwall.infrastructure.eventbus.EventBus
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.milliseconds

class PostgresEventBusTest : AbstractEventBusTest() {

    override lateinit var eventBus: EventBus

    private val postgresContainer: PostgreSQLContainer<Nothing>

    init {
        postgresContainer = PostgreSQLContainer<Nothing>(
            DockerImageName.parse("postgres:16-alpine")
        ).waitingFor(
            Wait.defaultWaitStrategy()
        )

        postgresContainer.start()
    }

    @BeforeTest
    fun setUp() {
        eventBus = PostgresEventBus(
            clientId = "test-client",
            jdbcUrl = postgresContainer.jdbcUrl,
            username = postgresContainer.username,
            password = postgresContainer.password,
            pollingInterval = 100.milliseconds
        )
    }
}