package com.fourthwall.infrastructure.eventbus.postgres.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientOffsetRepositoryTest {

    private lateinit var database: Database
    private lateinit var repository: ClientOffsetRepository

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
        repository = ClientOffsetRepository(database)
    }

    @AfterTest
    fun tearDown() {
        transaction(database) {
            // Clean up the database after each test
            ClientOffsetRepository.ClientOffset.deleteAll()
        }
    }

    @Test
    fun `test update and fetch offset`() {
        val clientId = "client1"
        val initialOffset = 5

        // Update the offset
        repository.updateOffset(clientId, initialOffset)

        // Fetch the offset
        val fetchedOffset = repository.fetchOffset(clientId)

        assertEquals(initialOffset, fetchedOffset)
    }

    @Test
    fun `test fetch offset for non-existent client`() {
        val clientId = "nonExistentClient"

        // Fetch the offset for a non-existent client
        val fetchedOffset = repository.fetchOffset(clientId)

        assertEquals(0, fetchedOffset)
    }

    @Test
    fun `test update existing client offset`() {
        val clientId = "client2"
        val initialOffset = 10
        val updatedOffset = 20

        // Insert initial offset
        repository.updateOffset(clientId, initialOffset)

        // Update the offset
        repository.updateOffset(clientId, updatedOffset)

        // Fetch the updated offset
        val fetchedOffset = repository.fetchOffset(clientId)

        assertEquals(updatedOffset, fetchedOffset)
    }
}