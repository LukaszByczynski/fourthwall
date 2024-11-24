package com.fourthwall.infrastructure.eventbus.postgres.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.transaction

data class EventBusEvent(val id: Int, val topic: String, val payload: String)

class EventsRepository(private val database: Database) {
    object Events : Table() {
        val id = integer("id").autoIncrement()
        val topic = varchar("topic", 255)
        val payload = text("payload")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Events)
        }
    }

    fun fetchFromOffset(offset: Int, limit: Int = 100): List<EventBusEvent> {
        return transaction(database) {
            Events
                .selectAll()
                .where(Events.id greater offset)
                .limit(limit)
                .map { EventBusEvent(it[Events.id], it[Events.topic], it[Events.payload]) }
        }
    }

    fun publish(topic: String, payload: String) {
        transaction(database) {
            Events.insert {
                it[Events.topic] = topic
                it[Events.payload] = payload
            }
        }
    }
}