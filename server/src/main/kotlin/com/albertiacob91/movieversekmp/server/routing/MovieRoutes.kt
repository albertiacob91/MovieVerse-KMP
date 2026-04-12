package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.dto.movies.MovieResponse
import com.albertiacob91.movieversekmp.server.remote.TmdbApi
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import com.typesafe.config.ConfigFactory

fun Route.movieRoutes() {
    val config = ConfigFactory.load()
    val apiKey = config.getString("tmdb.apiKey")
    val baseUrl = config.getString("tmdb.baseUrl")
    val tmdbApi = TmdbApi(apiKey, baseUrl)

    route("/movies") {
        get("/popular") {
            val movies = tmdbApi.getPopularMovies().map {
                MovieResponse(
                    id = it.id,
                    title = it.title,
                    posterUrl = it.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                    overview = it.overview,
                    releaseDate = it.releaseDate
                )
            }

            call.respond(HttpStatusCode.OK, movies)
        }

        get("/{id}") {
            val movieId = call.parameters.getOrFail("id").toInt()
            val movie = tmdbApi.getMovieDetail(movieId)

            call.respond(
                HttpStatusCode.OK,
                MovieResponse(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                    overview = movie.overview,
                    releaseDate = movie.releaseDate
                )
            )
        }
    }
}