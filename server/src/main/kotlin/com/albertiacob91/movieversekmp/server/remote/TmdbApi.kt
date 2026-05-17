package com.albertiacob91.movieversekmp.server.data.remote

import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbCreditsDto
import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbMovieDetailDto
import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbPopularResponseDto
import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbVideosDto
import com.albertiacob91.movieversekmp.server.data.remote.dto.TmdbWatchProvidersDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TmdbApi(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String
) {
    suspend fun getPopularMovies(page: Int = 1): TmdbPopularResponseDto {
        return client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
            parameter("page", page)
        }.body()
    }

    suspend fun searchMovies(query: String, page: Int = 1): TmdbPopularResponseDto {
        return client.get("$baseUrl/search/movie") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
            parameter("query", query)
            parameter("page", page)
        }.body()
    }

    suspend fun getMovieDetail(movieId: Int): TmdbMovieDetailDto {
        return client.get("$baseUrl/movie/$movieId") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }

    suspend fun getMovieCredits(movieId: Int): TmdbCreditsDto {
        return client.get("$baseUrl/movie/$movieId/credits") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }

    suspend fun getMovieVideos(movieId: Int): TmdbVideosDto {
        return client.get("$baseUrl/movie/$movieId/videos") {
            parameter("api_key", apiKey)
            parameter("language", "es-ES")
        }.body()
    }

    suspend fun getMovieWatchProviders(movieId: Int): TmdbWatchProvidersDto {
        return client.get("$baseUrl/movie/$movieId/watch/providers") {
            parameter("api_key", apiKey)
        }.body()
    }
}