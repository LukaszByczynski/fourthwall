package com.fourthwall.infrastructure.eventbus.postgres.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ClientOffsetRepository(private val database: Database) {
    object ClientOffset : Table("client_offset") {
        val clientId = varchar("client_id", 255)
        val offset = integer("offset")

        override val primaryKey = PrimaryKey(clientId)
    }

    init {
        transaction(database) {
            SchemaUtils.create(ClientOffset)
        }
    }

    fun updateOffset(clientId: String, offset: Int) {
        transaction {
            // Check if the user exists
            val has = ClientOffset
                .selectAll()
                .where { ClientOffset.clientId eq clientId }
                .singleOrNull()

            if (has != null) {
                ClientOffset.update({ ClientOffset.clientId eq clientId }) {
                    it[ClientOffset.offset] = offset
                }
            } else {
                ClientOffset.insert {
                    it[ClientOffset.clientId] = clientId
                    it[ClientOffset.offset] = offset
                }
            }
        }
    }

    fun fetchOffset(clientId: String): Int {
        return transaction {
            ClientOffset
                .select(ClientOffset.offset)
                .where { ClientOffset.clientId eq clientId }
                .mapNotNull { it[ClientOffset.offset] }
                .singleOrNull() ?: 0
        }
    }
}