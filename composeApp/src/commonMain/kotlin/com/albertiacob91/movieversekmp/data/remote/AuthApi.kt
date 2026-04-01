package com.albertiacob91.movieversekmp.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi {

    private val client = HttpClientFactory.client

    suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthResponseDto {
        val response = client.post("http://10.0.2.2:8081/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(username, email, password))
        }

        return if (response.status.value in 200..299) {
            response.body()
        } else {
            AuthResponseDto(message = "Register failed: ${response.status.value}")
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): AuthResponseDto {
        return client.post("http://10.0.2.2:8081/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequestDto(
                    email = email,
                    password = password
                )
            )
        }.body()
    }
}