package com.albertiacob91.movieversekmp.server.data.repository

import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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
                it[avatarUrl] = null
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
                    passwordHash = it[UsersTable.passwordHash],
                    avatarUrl = it[UsersTable.avatarUrl]
                )
            }
            .singleOrNull()
    }

    fun findById(id: UUID): UserRecord? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map {
                UserRecord(
                    id = it[UsersTable.id].toString(),
                    username = it[UsersTable.username],
                    email = it[UsersTable.email],
                    passwordHash = it[UsersTable.passwordHash],
                    avatarUrl = it[UsersTable.avatarUrl]
                )
            }
            .singleOrNull()
    }

    fun existsByUsername(username: String): Boolean = transaction {
        UsersTable.selectAll()
            .where { UsersTable.username eq username }
            .count() > 0
    }

    fun updateAvatar(userId: UUID, avatarBase64: String) {
        transaction {
            UsersTable.update({ UsersTable.id eq userId }) {
                it[avatarUrl] = avatarBase64
                it[updatedAt] = Instant.now()
            }
        }
    }
}


data class UserRecord(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val avatarUrl: String? = null
)