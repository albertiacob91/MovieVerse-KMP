package com.albertiacob91.movieversekmp.navigation

import androidx.compose.runtime.*
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen
import com.albertiacob91.movieversekmp.presentation.components.LoadingScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MoviesScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MovieDetailScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.FavoritesScreen
import com.albertiacob91.movieversekmp.presentation.screens.profile.ProfileScreen

@Composable
fun MovieVerseNavigation() {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var appScreen by remember { mutableStateOf<AppScreen?>(null) }
    var authScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

    LaunchedEffect(Unit) {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            appScreen = AppScreen.Auth
        } else {
            val me = runCatching { authApi.getMe(token) }.getOrNull()

            if (me != null) {
                appScreen = AppScreen.Home
            } else {
                sessionStorage.clearSession()
                appScreen = AppScreen.Auth
            }
        }
    }

    when (appScreen) {
        null -> {
            LoadingScreen()
        }

        AppScreen.Auth -> {
            when (authScreen) {
                AuthScreen.Login -> {
                    LoginScreen(
                        onNavigateToRegister = {
                            authScreen = AuthScreen.Register
                        },
                        onLoginClick = { email, password ->
                            runCatching {
                                val response = authApi.login(email, password)

                                if (!response.token.isNullOrBlank()) {
                                    sessionStorage.saveToken(response.token)
                                    appScreen = AppScreen.Home
                                }

                                response.message
                            }.getOrElse {
                                "Login error: ${it.message}"
                            }
                        }
                    )
                }

                AuthScreen.Register -> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            authScreen = AuthScreen.Login
                        },
                        onRegisterClick = { username, email, password ->
                            runCatching {
                                val response = authApi.register(username, email, password)
                                response.message
                            }.getOrElse {
                                "Register error: ${it.message}"
                            }
                        }
                    )
                }
            }
        }

        AppScreen.Home -> {
            var movieScreen by remember { mutableStateOf<MovieScreen>(MovieScreen.List) }

            when (val currentMovieScreen = movieScreen) {
                MovieScreen.List -> {
                    MoviesScreen(
                        onLogoutClick = {
                            sessionStorage.clearSession()
                            authScreen = AuthScreen.Login
                            appScreen = AppScreen.Auth
                        },
                        onMovieClick = { movieId ->
                            movieScreen = MovieScreen.Detail(movieId)
                        },
                        onFavoritesClick = {
                            movieScreen = MovieScreen.Favorites
                        },
                        onProfileClick = {
                            movieScreen = MovieScreen.Profile
                        }
                    )
                }

                is MovieScreen.Detail -> {
                    MovieDetailScreen(
                        movieId = currentMovieScreen.movieId,
                        onBackClick = {
                            movieScreen = MovieScreen.List
                        }
                    )
                }

                MovieScreen.Favorites -> {
                    FavoritesScreen(
                        onBackClick = {
                            movieScreen = MovieScreen.List
                        },
                        onMovieClick = { movieId ->
                            movieScreen = MovieScreen.Detail(movieId)
                        }
                    )
                }

                MovieScreen.Profile -> {
                    ProfileScreen(
                        onBackClick = {
                            movieScreen = MovieScreen.List
                        },
                        onLogoutClick = {
                            sessionStorage.clearSession()
                            authScreen = AuthScreen.Login
                            appScreen = AppScreen.Auth
                        }
                    )
                }
            }
        }
    }
}