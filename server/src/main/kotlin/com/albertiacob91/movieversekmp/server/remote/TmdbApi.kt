package com.albertiacob91.movieversekmp.server.remote

import com.albertiacob91.movieversekmp.server.dto.movies.TmdbMovieDto
import com.albertiacob91.movieversekmp.server.dto.movies.TmdbPopularResponseDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class TmdbApi(
    private val apiKey: String,
    private val baseUrl: String
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    suspend fun getPopularMovies(): List<TmdbMovieDto> {
        val response: TmdbPopularResponseDto = client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()

        return response.results
    }

    suspend fun getMovieDetail(movieId: Int): TmdbMovieDto {
        return client.get("$baseUrl/movie/$movieId") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }
}