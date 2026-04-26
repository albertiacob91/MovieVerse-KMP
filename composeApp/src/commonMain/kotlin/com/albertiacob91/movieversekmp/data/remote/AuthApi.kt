package com.albertiacob91.movieversekmp.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi {

    private val client = HttpClientFactory.client
    private val baseUrl = getBaseUrl()

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthResponseDto {
        val response = client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(username, email, password))
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            when (response.status.value) {
                409 -> AuthResponseDto(message = "Email o username ya registrado")
                401 -> AuthResponseDto(message = "Credenciales incorrectas")
                else -> AuthResponseDto(message = "Error: ${response.status.value}")
            }
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): AuthResponseDto {
        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(email, password))
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            when (response.status.value) {
                401 -> AuthResponseDto(message = "Credenciales incorrectas")
                409 -> AuthResponseDto(message = "Conflicto en login")
                else -> AuthResponseDto(message = "Login failed: ${response.status.value}")
            }
        }
    }

    suspend fun getMe(token: String): MeResponseDto? {
        val response = client.get("$baseUrl/auth/me") {
            header("Authorization", "Bearer $token")
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getPopularMovies(page: Int = 1): List<MovieDto> {
        val response = client.get("$baseUrl/movies/popular") {
            parameter("page", page)
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            emptyList()
        }
    }

    suspend fun getMovieDetail(movieId: Int): MovieDto? {
        val response = client.get("$baseUrl/movies/$movieId")

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getFavorites(token: String): List<FavoriteDto> {
        val response = client.get("$baseUrl/favorites") {
            header("Authorization", "Bearer $token")
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            emptyList()
        }
    }

    suspend fun addFavorite(token: String, movieId: Int): Boolean {
        val response = client.post("$baseUrl/favorites") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(FavoriteRequestDto(movieId))
        }

        return response.status.value in 200..299
    }

    suspend fun removeFavorite(token: String, movieId: Int): Boolean {
        val response = client.delete("$baseUrl/favorites/$movieId") {
            header("Authorization", "Bearer $token")
        }

        return response.status.value in 200..299
    }

    suspend fun getComments(movieId: Int): List<CommentDto> {
        val response = client.get("$baseUrl/comments/$movieId")

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            emptyList()
        }
    }

    suspend fun addComment(token: String, movieId: Int, content: String): Boolean {
        val response = client.post("$baseUrl/comments") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CommentRequestDto(movieId, content))
        }

        return response.status.value in 200..299
    }

    suspend fun searchMovies(query: String, page: Int = 1): List<MovieDto> {
        val response = client.get("$baseUrl/movies/search") {
            parameter("query", query)
            parameter("page", page)
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            emptyList()
        }
    }

    suspend fun isFavorite(token: String, movieId: Int): Boolean {
        val favorites = getFavorites(token)
        return favorites.any { it.movieId == movieId }
    }
}