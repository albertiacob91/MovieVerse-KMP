package com.albertiacob91.movieversekmp.server.data.repository

import com.albertiacob91.movieversekmp.server.data.model.SessionsTable
import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SessionRepository {

    fun createSession(userId: UUID): String {
        val sessionId = UUID.randomUUID()
        val token = UUID.randomUUID().toString()
        val now = Instant.now()
        val expiresAt = now.plus(7, ChronoUnit.DAYS)

        transaction {
            SessionsTable.insert {
                it[id] = sessionId
                it[SessionsTable.userId] = userId
                it[SessionsTable.token] = token
                it[SessionsTable.expiresAt] = expiresAt
                it[createdAt] = now
            }
        }

        return token
    }

    fun findUserByToken(token: String): UserRecord? = transaction {
        val now = Instant.now()

        SessionsTable
            .join(
                UsersTable,
                org.jetbrains.exposed.sql.JoinType.INNER,
                onColumn = SessionsTable.userId,
                otherColumn = UsersTable.id
            )
            .selectAll()
            .where {
                (SessionsTable.token eq token) and
                        (SessionsTable.expiresAt greaterEq now)
            }
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
}