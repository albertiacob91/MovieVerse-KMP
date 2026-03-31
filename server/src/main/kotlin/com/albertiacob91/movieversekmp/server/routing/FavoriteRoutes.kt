package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.data.repository.FavoriteRepository
import com.albertiacob91.movieversekmp.server.data.repository.SessionRepository
import com.albertiacob91.movieversekmp.server.dto.FavoriteRequest
import com.albertiacob91.movieversekmp.server.dto.FavoriteResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import java.util.UUID

fun Route.favoriteRoutes() {
    val sessionRepository = SessionRepository()
    val favoriteRepository = FavoriteRepository()

    route("/favorites") {
        get {
            val authHeader = call.request.header("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Missing or invalid token"))
                return@get
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val user = sessionRepository.findUserByToken(token)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid or expired session"))
                return@get
            }

            val favorites = favoriteRepository.getFavoritesByUser(UUID.fromString(user.id))
                .map {
                    FavoriteResponse(
                        id = it.id,
                        movieId = it.movieId,
                        userId = it.userId
                    )
                }

            call.respond(HttpStatusCode.OK, favorites)
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

            val request = call.receive<FavoriteRequest>()
            val userId = UUID.fromString(user.id)

            if (favoriteRepository.exists(userId, request.movieId)) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to "Movie already in favorites"))
                return@post
            }

            val favoriteId = favoriteRepository.addFavorite(userId, request.movieId)

            call.respond(
                HttpStatusCode.Created,
                FavoriteResponse(
                    id = favoriteId.toString(),
                    movieId = request.movieId,
                    userId = user.id
                )
            )
        }

        delete("/{movieId}") {
            val authHeader = call.request.header("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Missing or invalid token"))
                return@delete
            }

            val token = authHeader.removePrefix("Bearer ").trim()
            val user = sessionRepository.findUserByToken(token)

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid or expired session"))
                return@delete
            }

            val movieId = call.parameters.getOrFail("movieId").toInt()
            val deleted = favoriteRepository.deleteFavorite(UUID.fromString(user.id), movieId)

            if (!deleted) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Favorite not found"))
                return@delete
            }

            call.respond(HttpStatusCode.OK, mapOf("message" to "Favorite deleted"))
        }
    }
}