package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.data.model.ForumChatsTable
import com.albertiacob91.movieversekmp.server.data.model.ForumMessagesTable
import com.albertiacob91.movieversekmp.server.data.model.SessionsTable
import com.albertiacob91.movieversekmp.server.data.model.UsersTable
import com.albertiacob91.movieversekmp.server.dto.CreateForumChatRequest
import com.albertiacob91.movieversekmp.server.dto.CreateForumMessageRequest
import com.albertiacob91.movieversekmp.server.dto.ForumChatResponse
import com.albertiacob91.movieversekmp.server.dto.ForumMessageResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.deleteWhere
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
                            userId = it[ForumChatsTable.userId].toString(),
                            createdAt = it[ForumChatsTable.createdAt].toString(),
                            avatarUrl = it[UsersTable.avatarUrl]
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

            val userRow = transaction {
                UsersTable
                    .selectAll()
                    .first { row -> row[UsersTable.id] == sessionUserId }
            }

            call.respond(
                HttpStatusCode.Created,
                ForumChatResponse(
                    id = newId.toString(),
                    title = request.title.trim(),
                    createdBy = userRow[UsersTable.username],
                    userId = sessionUserId.toString(),
                    createdAt = createdAt.toString(),
                    avatarUrl = userRow[UsersTable.avatarUrl]
                )
            )
        }

        delete("/chats/{chatId}") {
            val authHeader = call.request.headers["Authorization"]

            if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Token missing"))
                return@delete
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val chatId = call.parameters["chatId"]?.let {
                runCatching { UUID.fromString(it) }.getOrNull()
            }

            if (chatId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid chat id"))
                return@delete
            }

            val sessionUserId = transaction {
                SessionsTable
                    .selectAll()
                    .firstOrNull { row -> row[SessionsTable.token] == token }
                    ?.get(SessionsTable.userId)
            }

            if (sessionUserId == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid session"))
                return@delete
            }

            val chatOwnerId = transaction {
                ForumChatsTable.selectAll()
                    .firstOrNull { row -> row[ForumChatsTable.id] == chatId }
                    ?.get(ForumChatsTable.userId)
            }

            if (chatOwnerId == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Chat not found"))
                return@delete
            }

            if (chatOwnerId != sessionUserId) {
                call.respond(HttpStatusCode.Forbidden, mapOf("message" to "Not authorized"))
                return@delete
            }

            transaction {
                ForumMessagesTable.deleteWhere { ForumMessagesTable.chatId eq chatId }
                ForumChatsTable.deleteWhere { ForumChatsTable.id eq chatId }
            }

            call.respond(HttpStatusCode.OK, mapOf("message" to "Chat deleted"))
        }

        get("/chats/{chatId}/messages") {
            val chatId = call.parameters["chatId"]?.let {
                runCatching { UUID.fromString(it) }.getOrNull()
            }

            if (chatId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid chat id"))
                return@get
            }

            val messages = transaction {
                (ForumMessagesTable innerJoin UsersTable)
                    .selectAll()
                    .where { ForumMessagesTable.chatId eq chatId }
                    .orderBy(ForumMessagesTable.createdAt, SortOrder.ASC)
                    .map {
                        ForumMessageResponse(
                            id = it[ForumMessagesTable.id].toString(),
                            chatId = it[ForumMessagesTable.chatId].toString(),
                            content = it[ForumMessagesTable.content],
                            username = it[UsersTable.username],
                            userId = it[UsersTable.id].toString(),
                            avatarUrl = it[UsersTable.avatarUrl],
                            createdAt = it[ForumMessagesTable.createdAt].toString()
                        )
                    }
            }

            call.respond(HttpStatusCode.OK, messages)
        }

        post("/chats/{chatId}/messages") {
            val authHeader = call.request.headers["Authorization"]

            if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Token missing"))
                return@post
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val chatId = call.parameters["chatId"]?.let {
                runCatching { UUID.fromString(it) }.getOrNull()
            }

            if (chatId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid chat id"))
                return@post
            }

            val request = call.receive<CreateForumMessageRequest>()

            if (request.content.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Content is required"))
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

            val chatExists = transaction {
                ForumChatsTable
                    .selectAll()
                    .any { row -> row[ForumChatsTable.id] == chatId }
            }

            if (!chatExists) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Chat not found"))
                return@post
            }

            val createdAt = Instant.now()
            val newId = UUID.randomUUID()

            transaction {
                ForumMessagesTable.insert {
                    it[ForumMessagesTable.id] = newId
                    it[ForumMessagesTable.chatId] = chatId
                    it[ForumMessagesTable.userId] = sessionUserId
                    it[ForumMessagesTable.content] = request.content.trim()
                    it[ForumMessagesTable.createdAt] = createdAt
                }
            }

            val userRow = transaction {
                UsersTable
                    .selectAll()
                    .first { row -> row[UsersTable.id] == sessionUserId }
            }

            call.respond(
                HttpStatusCode.Created,
                ForumMessageResponse(
                    id = newId.toString(),
                    chatId = chatId.toString(),
                    content = request.content.trim(),
                    username = userRow[UsersTable.username],
                    userId = sessionUserId.toString(),
                    avatarUrl = userRow[UsersTable.avatarUrl],
                    createdAt = createdAt.toString()
                )
            )
        }
    }
}
