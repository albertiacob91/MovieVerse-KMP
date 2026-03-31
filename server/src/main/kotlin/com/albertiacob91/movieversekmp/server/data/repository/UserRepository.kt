package com.albertiacob91.movieversekmp.server.data.repository

import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class UserRepository {

    fun existsByEmail(email: String): Boolean = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .count() > 0
    }

    fun createUser(username: String, email: String, passwordHash: String): UUID {
        val userId = UUID.randomUUID()
        val now = Instant.now()

        transaction {
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.username] = username
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
                it[createdAt] = now
                it[updatedAt] = now
            }
        }

        return userId
    }

    fun findByEmail(email: String): UserRecord? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map {
                UserRecord(
                    id = it[UsersTable.id].toString(),
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash]
                )
            }
            .singleOrNull()
    }
}

data class UserRecord(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String
)