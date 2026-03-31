package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.data.repository.CommentRepository
import com.albertiacob91.movieversekmp.server.data.repository.SessionRepository
import com.albertiacob91.movieversekmp.server.dto.CommentRequest
import com.albertiacob91.movieversekmp.server.dto.CommentResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import java.util.UUID

fun Route.commentRoutes() {
    val sessionRepository = SessionRepository()
    val commentRepository = CommentRepository()

    route("/comments") {
        get("/{movieId}") {
            val movieId = call.parameters.getOrFail("movieId").toInt()

            val comments = commentRepository.getCommentsByMovie(movieId)
                .map {
                    CommentResponse(
                        id = it.id,
                        movieId = it.movieId,
                        userId = it.userId,
                        username = it.username,
                        content = it.content
                    )
                }

            call.respond(HttpStatusCode.OK, comments)
        }

        post {
            val authHeader = call.request.header("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Missing or invalid token"))
                return@post
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val user = sessionRepository.findUserByToken(token)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid or expired session"))
                return@post
            }

            val request = call.receive<CommentRequest>()
            val userId = UUID.fromString(user.id)

            val commentId = commentRepository.addComment(
                userId = userId,
                movieId = request.movieId,
                content = request.content
            )

            call.respond(
                HttpStatusCode.Created,
                CommentResponse(
                    id = commentId.toString(),
                    movieId = request.movieId,
                    userId = user.id,
                    username = user.username,
                    content = request.content
                )
            )
        }
    }
}