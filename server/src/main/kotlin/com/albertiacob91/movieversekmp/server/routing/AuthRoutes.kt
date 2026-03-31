package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.auth.PasswordHasher
import com.albertiacob91.movieversekmp.server.data.repository.UserRepository
import com.albertiacob91.movieversekmp.server.dto.AuthResponse
import com.albertiacob91.movieversekmp.server.dto.LoginRequest
import com.albertiacob91.movieversekmp.server.dto.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    val userRepository = UserRepository()

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

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    message = "Login successful",
                    userId = user.id,
                    username = user.username
                )
            )
        }
    }
}