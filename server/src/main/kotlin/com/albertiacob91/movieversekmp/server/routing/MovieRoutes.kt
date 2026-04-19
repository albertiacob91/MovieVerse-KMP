package com.albertiacob91.movieversekmp.server.routing

import com.albertiacob91.movieversekmp.server.data.remote.TmdbApi
import com.albertiacob91.movieversekmp.server.data.remote.response.MovieResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.movieRoutes(tmdbApi: TmdbApi) {
    route("/movies") {

        get("/popular") {
            val movies = tmdbApi.getPopularMovies().results.take(20).map { movie ->
                val detail = runCatching { tmdbApi.getMovieDetail(movie.id) }.getOrNull()

                MovieResponse(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                    overview = movie.overview,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage,
                    runtime = detail?.runtime,
                    genres = detail?.genres?.map { it.name } ?: emptyList()
                )
            }

            call.respond(HttpStatusCode.OK, movies)
        }

        get("/search") {
            val query = call.request.queryParameters["query"].orEmpty().trim()

            if (query.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Query is required"))
                return@get
            }

            val movies = tmdbApi.searchMovies(query).results.take(20).map { movie ->
                val detail = runCatching { tmdbApi.getMovieDetail(movie.id) }.getOrNull()

                MovieResponse(
                    id = movie.id,
                    title = movie.title,
                    posterUrl = movie.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                    overview = movie.overview,
                    releaseDate = movie.releaseDate,
                    voteAverage = movie.voteAverage,
                    runtime = detail?.runtime,
                    genres = detail?.genres?.map { it.name } ?: emptyList()
                )
            }

            call.respond(HttpStatusCode.OK, movies)
        }

        get("/{id}") {
            val movieId = call.parameters["id"]?.toIntOrNull()

            if (movieId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid movie id"))
                return@get
            }

            val movie = tmdbApi.getMovieDetail(movieId)

            val response = MovieResponse(
                id = movie.id,
                title = movie.title,
                posterUrl = movie.posterPath?.let { path -> "https://image.tmdb.org/t/p/w500$path" },
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage,
                runtime = movie.runtime,
                genres = movie.genres.map { it.name }
            )

            call.respond(HttpStatusCode.OK, response)
        }
    }
}