package com.fourthwall.infrastructure.eventbus.postgres.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.*

class EventsRepositoryTest {

    private lateinit var database: Database
    private lateinit var repository: EventsRepository

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
        database = Database.connect(
            postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )

        repository = EventsRepository(database)
    }

    @AfterTest
    fun tearDown() {
        transaction(database) {
            // Clean up the database after each test
            EventsRepository.Events.deleteAll()
            exec("ALTER SEQUENCE events_id_seq RESTART WITH 1")
        }
    }

    @Test
    fun `test publish and fetch events`() {
        val topic = "testTopic"
        val payload = "testPayload"

        // Publish an event
        repository.publish(topic, payload)

        // Fetch events from offset 0
        val events = repository.fetchFromOffset(0)

        assertEquals(1, events.size)
        assertEquals(topic, events[0].topic)
        assertEquals(payload, events[0].payload)
    }

    @Test
    fun `test fetch from offset with limit`() {
        // Publish multiple events
        for (i in 1..5) {
            repository.publish("topic$i", "payload$i")
        }

        // Fetch events from offset 0 with a limit of 3
        val events = repository.fetchFromOffset(0, limit = 3)

        assertEquals(3, events.size)
        assertTrue(events.all { it.id in 1..3 }) // Ensure the IDs are within the expected range
    }

    @Test
    fun `test fetch from offset with higher offset`() {
        // Publish multiple events
        for (i in 1..5) {
            repository.publish("topic$i", "payload$i")
        }

        // Fetch events from offset 3
        val events = repository.fetchFromOffset(3)

        assertEquals(2, events.size) // IDs 4 and 5 should be fetched
        assertTrue(events.all { it.id in 4..5 }) // Ensure the IDs are correct
    }

    @Test
    fun `test fetch from offset with no events`() {
        // Fetch events from an offset where no events exist
        val events = repository.fetchFromOffset(10)

        assertTrue(events.isEmpty()) // Should return an empty list
    }
}