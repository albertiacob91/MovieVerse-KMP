package com.albertiacob91.movieversekmp.server.data.remote

import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbMovieDetailDto
import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbPopularResponseDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class TmdbApi(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String
) {

    suspend fun getPopularMovies(): TmdbPopularResponseDto {
        return client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }

    suspend fun searchMovies(query: String): TmdbPopularResponseDto {
        return client.get("$baseUrl/search/movie") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
            parameter("query", query)
        }.body()
    }

    suspend fun getMovieDetail(movieId: Int): TmdbMovieDetailDto {
        return client.get("$baseUrl/movie/$movieId") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }
}