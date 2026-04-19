package com.albertiacob91.movieversekmp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.albertiacob91.movieversekmp.data.local.SessionStorage
import com.albertiacob91.movieversekmp.data.remote.AuthApi
import com.albertiacob91.movieversekmp.presentation.components.LoadingScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.LoginScreen
import com.albertiacob91.movieversekmp.presentation.screens.auth.RegisterScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.FavoritesScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MovieDetailScreen
import com.albertiacob91.movieversekmp.presentation.screens.movies.MoviesScreen
import com.albertiacob91.movieversekmp.presentation.screens.profile.ProfileScreen

private enum class RootScreen {
    Auth,
    Home
}

private enum class AuthFlowScreen {
    Login,
    Register
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieVerseNavigation() {
    val authApi = remember { AuthApi() }
    val sessionStorage = remember { SessionStorage() }

    var rootScreen by remember { mutableStateOf<RootScreen?>(null) }
    var authFlowScreen by remember { mutableStateOf(AuthFlowScreen.Login) }
    var movieScreen by remember { mutableStateOf<MovieScreen>(MovieScreen.Home) }
    var searchVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val token = sessionStorage.getToken()

        if (token.isNullOrBlank()) {
            rootScreen = RootScreen.Auth
        } else {
            val me = runCatching { authApi.getMe(token) }.getOrNull()

            if (me != null) {
                rootScreen = RootScreen.Home
            } else {
                sessionStorage.clearSession()
                rootScreen = RootScreen.Auth
            }
        }
    }

    when (rootScreen) {
        null -> {
            LoadingScreen()
        }

        RootScreen.Auth -> {
            when (authFlowScreen) {
                AuthFlowScreen.Login -> {
                    LoginScreen(
                        onNavigateToRegister = {
                            authFlowScreen = AuthFlowScreen.Register
                        },
                        onLoginClick = { email, password ->
                            runCatching {
                                val response = authApi.login(email, password)

                                if (!response.token.isNullOrBlank()) {
                                    sessionStorage.saveToken(response.token)
                                    movieScreen = MovieScreen.Home
                                    searchVisible = false
                                    rootScreen = RootScreen.Home
                                }

                                response.message
                            }.getOrElse {
                                "Login error: ${it.message}"
                            }
                        }
                    )
                }

                AuthFlowScreen.Register -> {
                    RegisterScreen(
                        onNavigateToLogin = {
                            authFlowScreen = AuthFlowScreen.Login
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

        RootScreen.Home -> {
            when (val currentMovieScreen = movieScreen) {
                is MovieScreen.Detail -> {
                    MovieDetailScreen(
                        movieId = currentMovieScreen.movieId,
                        onBackClick = {
                            movieScreen = MovieScreen.Home
                        }
                    )
                }

                MovieScreen.Home,
                MovieScreen.Favorites,
                MovieScreen.Profile -> {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        when (movieScreen) {
                                            MovieScreen.Home -> "MovieVerse"
                                            MovieScreen.Favorites -> "Favoritas"
                                            MovieScreen.Profile -> "Perfil"
                                            is MovieScreen.Detail -> ""
                                        }
                                    )
                                },
                                actions = {
                                    if (movieScreen == MovieScreen.Home) {
                                        IconButton(
                                            onClick = {
                                                searchVisible = !searchVisible
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Buscar"
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Home,
                                    onClick = {
                                        movieScreen = MovieScreen.Home
                                    },
                                    icon = {
                                        Icon(Icons.Default.Home, contentDescription = "Home")
                                    },
                                    label = { Text("Home") }
                                )

                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Favorites,
                                    onClick = {
                                        movieScreen = MovieScreen.Favorites
                                    },
                                    icon = {
                                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                                    },
                                    label = { Text("Favoritos") }
                                )

                                NavigationBarItem(
                                    selected = movieScreen == MovieScreen.Profile,
                                    onClick = {
                                        movieScreen = MovieScreen.Profile
                                    },
                                    icon = {
                                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                                    },
                                    label = { Text("Perfil") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        when (movieScreen) {
                            MovieScreen.Home -> {
                                MoviesScreen(
                                    contentPadding = innerPadding,
                                    searchVisible = searchVisible,
                                    onMovieClick = { movieId ->
                                        movieScreen = MovieScreen.Detail(movieId)
                                    }
                                )
                            }

                            MovieScreen.Favorites -> {
                                FavoritesScreen(
                                    contentPadding = innerPadding,
                                    onMovieClick = { movieId ->
                                        movieScreen = MovieScreen.Detail(movieId)
                                    }
                                )
                            }

                            MovieScreen.Profile -> {
                                ProfileScreen(
                                    contentPadding = innerPadding,
                                    onLogoutClick = {
                                        sessionStorage.clearSession()
                                        authFlowScreen = AuthFlowScreen.Login
                                        movieScreen = MovieScreen.Home
                                        searchVisible = false
                                        rootScreen = RootScreen.Auth
                                    }
                                )
                            }

                            is MovieScreen.Detail -> Unit
                        }
                    }
                }
            }
        }
    }
}