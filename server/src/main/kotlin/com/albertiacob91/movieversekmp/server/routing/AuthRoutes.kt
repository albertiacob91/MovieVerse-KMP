package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.auth.PasswordHasher
import com.albertiacob91.movieversekmp.server.data.repository.SessionRepository
import com.albertiacob91.movieversekmp.server.data.repository.UserRepository
import com.albertiacob91.movieversekmp.server.dto.AuthResponse
import com.albertiacob91.movieversekmp.server.dto.LoginRequest
import com.albertiacob91.movieversekmp.server.dto.MeResponse
import com.albertiacob91.movieversekmp.server.dto.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.authRoutes() {
    val userRepository = UserRepository()
    val sessionRepository = SessionRepository()

    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            if (userRepository.existsByEmail(request.email)) {
                call.respond(
                    HttpStatusCode.Conflict,
                    AuthResponse(message = "Email already registered")
                )
                return@post
            }

            val hashedPassword = PasswordHasher.hash(request.password)
            val userId = userRepository.createUser(
                username = request.username,
                email = request.email,
                passwordHash = hashedPassword
            )

            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    message = "User registered successfully",
                    userId = userId.toString(),
                    username = request.username
                )
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = userRepository.findByEmail(request.email)

            if (user == null || !PasswordHasher.verify(request.password, user.passwordHash)) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    AuthResponse(message = "Invalid credentials")
                )
                return@post
            }

            val token = sessionRepository.createSession(UUID.fromString(user.id))

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    message = "Login successful",
                    userId = user.id,
                    username = user.username,
                    token = token
                )
            )
        }

        get("/me") {
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

            call.respond(
                HttpStatusCode.OK,
                MeResponse(
                    id = user.id,
                    username = user.username,
                    email = user.email
                )
            )
        }
    }
}