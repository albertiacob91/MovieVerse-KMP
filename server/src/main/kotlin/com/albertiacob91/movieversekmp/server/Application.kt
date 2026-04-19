package com.albertiacob91.movieversekmp.server

import com.albertiacob91.movieversekmp.server.config.DatabaseFactory
import com.albertiacob91.movieversekmp.server.data.remote.TmdbApi
import com.albertiacob91.movieversekmp.server.routing.authRoutes
import com.albertiacob91.movieversekmp.server.routing.commentRoutes
import com.albertiacob91.movieversekmp.server.routing.favoriteRoutes
import com.albertiacob91.movieversekmp.server.routing.movieRoutes
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import io.ktor.server.application.install

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()

    install(ServerContentNegotiation) {
        json()
    }

    val tmdbClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }
    }

    val tmdbApi = TmdbApi(
        client = tmdbClient,
        apiKey = environment.config.property("tmdb.apiKey").getString(),
        baseUrl = environment.config.property("tmdb.baseUrl").getString()
    )

    routing {
        get("/") {
            call.respond(mapOf("message" to "MovieVerse server running"))
        }

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        authRoutes()
        favoriteRoutes()
        commentRoutes()
        movieRoutes(tmdbApi)
    }
}