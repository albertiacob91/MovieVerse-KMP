package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.data.model.ForumChatsTable
import com.albertiacob91.movieversekmp.server.data.model.SessionsTable
import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import com.albertiacob91.movieversekmp.server.dto.CreateForumChatRequest
import com.albertiacob91.movieversekmp.server.dto.ForumChatResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

fun Route.forumRoutes() {
    route("/forum") {

        get("/chats") {
            val chats = transaction {
                (ForumChatsTable innerJoin UsersTable)
                    .selectAll()
                    .orderBy(ForumChatsTable.createdAt, SortOrder.DESC)
                    .map {
                        ForumChatResponse(
                            id = it[ForumChatsTable.id].toString(),
                            title = it[ForumChatsTable.title],
                            createdBy = it[UsersTable.username],
                            createdAt = it[ForumChatsTable.createdAt].toString()
                        )
                    }
            }

            call.respond(HttpStatusCode.OK, chats)
        }

        post("/chats") {
            val authHeader = call.request.headers["Authorization"]

            if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Token missing"))
                return@post
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val request = call.receive<CreateForumChatRequest>()

            if (request.title.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Title is required"))
                return@post
            }

            val sessionUserId = transaction {
                SessionsTable
                    .selectAll()
                    .firstOrNull { row -> row[SessionsTable.token] == token }
                    ?.get(SessionsTable.userId)
            }

            if (sessionUserId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid session"))
                return@post
            }

            val createdAt = Instant.now()
            val newId = UUID.randomUUID()

            transaction {
                ForumChatsTable.insert {
                    it[ForumChatsTable.id] = newId
                    it[ForumChatsTable.title] = request.title.trim()
                    it[ForumChatsTable.userId] = sessionUserId
                    it[ForumChatsTable.createdAt] = createdAt
                }
            }

            val username = transaction {
                UsersTable
                    .selectAll()
                    .first { row -> row[UsersTable.id] == sessionUserId }[UsersTable.username]
            }

            call.respond(
                HttpStatusCode.Created,
                ForumChatResponse(
                    id = newId.toString(),
                    title = request.title.trim(),
                    createdBy = username,
                    createdAt = createdAt.toString()
                )
            )
        }
    }
}